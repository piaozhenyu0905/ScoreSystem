package com.system.assessment.service.Impl;

import com.system.assessment.constants.ProcessType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.service.ProcessService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ScheduledTask {

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public ProcessService processService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void ScheduledTasks() {
        EvaluateProcess newEvaluateProcess = evaluateMapper.findNewEvaluateProcess();
        if(newEvaluateProcess == null){
            return;
        }
        Integer evaluateStep = newEvaluateProcess.getEvaluateStep();
        LocalDate endDate = newEvaluateProcess.getEndDate();
        if(endDate.isBefore(LocalDate.now())){
            //当前环节的截止日期已经到了，需要自动进入到下一个环节
            if(evaluateStep.equals(ProcessType.BuildRelationships.getCode())){
                processService.RelationConfirmedPublic();
            }else if(evaluateStep.equals(ProcessType.Evaluation.getCode())){
                processService.ScoringOver();
            }else if(evaluateStep.equals(ProcessType.ResultConsultation.getCode())){
                processService.StatisticsOver();
            }else {
                //第四个环节则不做处理
            }
        }else {
            return;
        }
    }
}
