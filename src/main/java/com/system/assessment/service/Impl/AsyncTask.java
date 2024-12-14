package com.system.assessment.service.Impl;

import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.pojo.User;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.ScoringBoardService;
import com.system.assessment.template.panelTemplate;
import com.system.assessment.vo.PanelScoreBoardVO;
import com.system.assessment.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AsyncTask {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public EmailService emailService;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public EvaluateService evaluateService;

    @Autowired
    public ScoringBoardService scoringBoardService;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public TodoListMapper todoListMapper;

    @Async("emailExecutor")
    public void sendEmailToAllForPanel(){

            List<Integer> allEvaluated = relationshipMapper.findAllEvaluated();
            if(allEvaluated == null){
                return;
            }
            allEvaluated.forEach(evaluatedId->{
                String name = null;
                try {
                    User basicInfoBySelfId = userMapper.findBasicInfoBySelfId(evaluatedId);
                    //如果该用户已经被删了则不发邮件
                    if(basicInfoBySelfId == null){
                        return;
                    }
                    if(basicInfoBySelfId.getIsDelete() != null && basicInfoBySelfId.getIsDelete()){
                        return;
                    }
                    name = basicInfoBySelfId.getName();
                    String email = basicInfoBySelfId.getEmail();
                    PanelScoreBoardVO panelScoreBoardVO = scoringBoardService.selfPanel(evaluatedId);
                    if (panelScoreBoardVO == null) {
                        return;
                    }
                    String lxyz = panelScoreBoardVO.getLxyz();
                    Double lxyzTotalScore = panelScoreBoardVO.getLxyzTotalScore();
                    String business = panelScoreBoardVO.getBusiness();
                    Double businessTotalScore = panelScoreBoardVO.getBusinessTotalScore();
                    Double totalScore = panelScoreBoardVO.getTotalScore();
                    String context = evaluateService.evaluateTableInfo().getOpeningRemarks();
                    EvaluateTable evaluateTable = evaluateService.evaluateTableInfo();
                    String title = evaluateTable.getTitle();
                    if(title == null || title.equals("")){
                        title = "个人成长报告";
                    }
                    String content = panelTemplate.htmlTemplate(title, name, context, totalScore, lxyz, lxyzTotalScore, business, businessTotalScore);
                    String subject = "成长评估结果发布";
                    emailService.sendMessageHTML(email, subject, content);
                    try {
                        Thread.sleep(1000);
                    }catch (Exception e){

                    }
                }catch (Exception e) {
                    log.error(name + "的成长评估报告发送出现异常!" + "时间为"+ LocalDateTime.now());
                    // 记录异常或处理异常，可以选择继续执行
                    log.error(e.getMessage());// 记录日志
                }
            });
        }

    @Async("emailExecutor")
    public void noticeAllNotCompleted(){
        //1.找出所有未打完分的人的邮箱
        Integer newEpoch = evaluateMapper.findNewEpoch();
        List<Integer> allNotCompletedId = todoListMapper.findAllNotCompleted(newEpoch);
        if(allNotCompletedId == null){
            return;
        }
        List<UserVO> allNotCompleted = new ArrayList<>();
        for (int i = 0; i < allNotCompletedId.size();i++){
            Integer userId = allNotCompletedId.get(i);
            User basicInfoBySelfId = userMapper.findBasicInfoBySelfId(userId);
            UserVO userVO = new UserVO();
            userVO.setId(basicInfoBySelfId.getId());
            userVO.setEmail(basicInfoBySelfId.getEmail());
            userVO.setName(basicInfoBySelfId.getName());
            allNotCompleted.add(userVO);
        }

        LocalDate endDate = evaluateMapper.findEndDate(newEpoch);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
        // 格式化LocalDate为String
        String deadline = endDate.format(formatter);

        for (int index = 0; index < allNotCompleted.size(); index++){
            UserVO userVO = allNotCompleted.get(index);
            String content = "请您尽快前往成长评估系统进行评分，截至日期为" + deadline;
            String subject = "评分提醒!";
            emailService.sendMessageHTML(userVO.getEmail(), subject, content);
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }
    }

    @Async("emailExecutor")
    public void sendEmailToEvaluator(Integer epoch, Map<Integer, String> map){

        LocalDate endDate = evaluateMapper.findEndDate(epoch);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");

        // 格式化LocalDate为String
        String deadline = endDate.format(formatter);

        List<String> emails = new ArrayList();
        for (Integer evalutorId : map.keySet()) {
            emails.add(map.get(evalutorId));
        }
        String subject = "评估关系矩阵发布";
        String content = "你好！本轮评估关系矩阵已发布，周边评议环节已经开始, 请您在" + deadline + "前前往成长评估系统进行周边评议!";
        sendManyMessage(emails, subject, content);
    }


    public void sendManyMessage(List<String> recipients, String subject, String content) {
        for (int index = 0; index < recipients.size(); index++){
            String to = recipients.get(index);
            emailService.sendMessage(to, subject, content);
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }
    }
};

