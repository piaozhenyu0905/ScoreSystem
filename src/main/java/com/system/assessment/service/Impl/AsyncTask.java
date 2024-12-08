package com.system.assessment.service.Impl;

import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.User;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.ScoringBoardService;
import com.system.assessment.template.panelTemplate;
import com.system.assessment.vo.PanelScoreBoardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
                    String content = panelTemplate.htmlTemplate(name, context, totalScore, lxyz, lxyzTotalScore, business, businessTotalScore);
                    String subject = "成长评估结果发布";
                    emailService.sendMessageHTML(email, subject, content);
                }catch (Exception e) {
                    log.error(name + "的成长评估报告发送出现异常!" + "时间为"+ LocalDateTime.now());
                    // 记录异常或处理异常，可以选择继续执行
                    log.error(e.getMessage());// 记录日志
                }
            });
        }

};

