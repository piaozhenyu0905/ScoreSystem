package com.system.assessment.service.Impl;

import com.system.assessment.constants.ProcessType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.ProcessService;
import com.system.assessment.vo.EmailVO;
import com.system.assessment.vo.NotCompletedSet;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduledTask {

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public ProcessService processService;

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public EmailService emailService;

    @Scheduled(cron = "0 0 8,13 * * ?")
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
                String content = "请您尽快前往成长评估系统进行评分";
                String subject = "评分提醒!";
                emailService.sendMessageHTML(email, subject, content);
            }
            //给hrbp发未完成打分的人的提醒
            String content = String.join("," ,evaluatorName) + "未完成打分任务!请您尽快督促他们完成打分任务!";
            String subject = "督促用户评分提醒!";
            emailService.sendMessageHTML(hrEmail, subject, content);
        }

    }



}
