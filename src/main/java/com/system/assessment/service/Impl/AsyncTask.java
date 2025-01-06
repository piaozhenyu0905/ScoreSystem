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
import com.system.assessment.vo.EmailVO;
import com.system.assessment.vo.NotCompletedSet;
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
public class AsyncTask  {

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
        List<NotCompletedSet> allNotCompletedId = todoListMapper.findAllNotCompleted(newEpoch);
        if(allNotCompletedId == null){
            return;
        }

        for (int index = 0; index < allNotCompletedId.size(); index++){
            List<String> evaluatorName = new ArrayList<>();
            NotCompletedSet notCompletedSet = allNotCompletedId.get(index);
            String hrEmail = notCompletedSet.getHrEmail();
            //给每个评估人发送打分提醒
            List<EmailVO> evaluators = notCompletedSet.getEvaluators();
            if(evaluators == null)
                continue;
            for (int idx = 0; idx < evaluators.size(); idx++){
                EmailVO emailVO = evaluators.get(idx);
                evaluatorName.add(emailVO.getName());
                String email = emailVO.getEmail();
                String content = panelTemplate.htmlNotice(emailVO.getName());
                String subject = "评分提醒！";
                emailService.sendMessageHTML(email, subject, content);
            }
            //给hrbp发未完成打分的人的提醒
            String content = "尊敬的" +notCompletedSet.getHrName() +"，您好！" +  String.join("，" ,evaluatorName) + "未完成打分任务！请您尽快督促他们完成打分任务！";
            String subject = "督促用户评分提醒！";
            emailService.sendMessageHTML(hrEmail, subject, content);
        }

    }


    public void sendEmailToEvaluator(Integer epoch, Map<Integer, String> map){

        List<String> emails = new ArrayList();
        for (Integer evalutorId : map.keySet()) {
            emails.add(map.get(evalutorId));
        }
        String subject = "评估关系矩阵发布";

        String content = panelTemplate.htmlRelationshipPublic();
        sendManyMessage(emails, subject, content);
    }


    public void sendManyMessage(List<String> recipients, String subject, String content) {
        for (int index = 0; index < recipients.size(); index++){
            String to = recipients.get(index);
            emailService.sendMessageHTML(to, subject, content);
            try {
                Thread.sleep(1000);
            }catch (Exception e){

            }
        }
    }
};

