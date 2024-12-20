package com.system.assessment.service.Impl;

import com.system.assessment.constants.OperationType;
import com.system.assessment.constants.ProcessType;
import com.system.assessment.constants.TaskType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.TodoList;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.ProcessService;
import com.system.assessment.service.ScoringBoardService;
import com.system.assessment.vo.RelationshipCheckVO;
import com.system.assessment.vo.ScoreDetailVO;
import com.system.assessment.vo.TodoListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcessServiceImpl implements ProcessService {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public EmailService emailService;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public ScoringBoardService scoringBoardService;

    @Autowired
    @Lazy
    public AsyncTask asyncTask;


    @Autowired
    public ProcessService processService;

    @Override
    public Integer findExtra(Integer epoch) {
        Integer step = ProcessType.ResultConsultation.getCode();
        return evaluateMapper.findExtra(epoch,step);
    }

    @Transactional
    @Override
    public void gotoNewStep(Integer newEpoch) {
        Integer newestEnableProcess = evaluateMapper.findNewestEnableProcess();
        Integer newProcess = newestEnableProcess + 1;
        evaluateMapper.endOldProcess(newestEnableProcess, newEpoch);
        evaluateMapper.gotoNewProcess(newProcess, newEpoch);
    }


    @Override
    public void StatisticsOver() {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        // 1.发送邮件
        asyncTask.sendEmailToAllForPanel();
        // 2.进入下一个环节
        processService.gotoNewStep(newEpoch);
    }

    @Override
    @Transactional
    public void ScoringOver() {
        Integer newEpoch = evaluateMapper.findNewEpoch();

        // 1.找出完成所有评议任务的人，将其未确认的任务都变成已确认
        List<ScoreDetailVO> scoringNewRoundIsNotEnable = todoListMapper.findScoringNewRoundIsNotEnable(newEpoch);
        List<ScoreDetailVO> filterList = scoringBoardService.filterData(scoringNewRoundIsNotEnable);
        if(filterList != null){
            filterList.forEach(scoreDetailVO -> {
                Integer userId = scoreDetailVO.getUserId();
                todoListMapper.enableTaskById(newEpoch, userId);
            });
        }

        //2.将所有未完成评议(operation=0)的任务置为无效(operation=2)
        todoListMapper.SetOperationInvalid(newEpoch, OperationType.INVALUED.getCode());

        //3.统计级联平均分，入库
        countAverageScoreByEpoch(newEpoch);
        //4.进入下一步
        processService.gotoNewStep(newEpoch);
    }

    @Override
    public void countAverageScoreByEpoch(Integer epoch){
        //1.删除当前epoch中所有统计的级联平均分
        todoListMapper.deleteAverageTable(epoch);
        //2.计算当前epoch中的级联平均分并入库
        scoringBoardService.countAverageScoreByEpoch(epoch);
    }

    @Override
    @Transactional
    public void RelationConfirmedPublic() {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        Map<Integer, String> map =new HashMap<>();
        // 1. 查找出当前所有有效的关系
        List<RelationshipCheckVO> allRelationship = relationshipMapper.findAllRelationship();
        // 2. 给每个关系创建一个评分代办列表
        allRelationship.forEach(relationship->{
            String email = relationship.getEvaluatorEmail();
            Integer evaluatorId = relationship.getEvaluatorId();
            Integer evaluatedId = relationship.getEvaluatedId();
            TodoListVO todoListIsExist = todoListMapper.findTodoListIsExist(evaluatorId, evaluatedId, newEpoch);
            if(todoListIsExist != null){
                return;
            }
            String evaluatedName = relationship.getEvaluatedName();
            String evaluatorName = relationship.getEvaluatorName();
            TodoList todoList = new TodoList();
            todoList.setType(TaskType.SurroundingEvaluation.getDescription());
            todoList.setPresentDate(LocalDateTime.now());
            todoList.setOperation(OperationType.NOTFINISHED.getCode());
            todoList.setRejectReason(null);
            todoList.setOwnerId(evaluatorId);
            todoList.setCompleteTime(null);
            todoList.setEvaluatorId(evaluatorId);
            todoList.setEvaluatedId(evaluatedId);
            todoList.setEvaluatorName(evaluatorName);
            todoList.setEvaluatedName(evaluatedName);
            todoList.setEnable(0); //分数设置待确认状态
            todoList.setEpoch(newEpoch);
            todoList.setConfidenceLevel(1.0);
            String Detail = "请完成对" + evaluatedName + "的周边评议";
            todoList.setDetail(Detail);
            todoListMapper.addTodoList(todoList);
            if(!map.containsKey(evaluatorId)){
                map.put(evaluatorId, email);
            }
        });
        //3.邮件通知所有涉及的员工去完成
        asyncTask.sendEmailToEvaluator(newEpoch, map);
        // 4.进入下一个环节
        processService.gotoNewStep(newEpoch);
    }

}
