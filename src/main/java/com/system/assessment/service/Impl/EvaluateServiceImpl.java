package com.system.assessment.service.Impl;

import com.system.assessment.constants.Guideline;
import com.system.assessment.constants.OperationType;
import com.system.assessment.constants.ProcessType;
import com.system.assessment.exception.CustomException;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.RelationshipMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.pojo.Score;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class EvaluateServiceImpl implements EvaluateService {

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public RelationshipMapper relationshipMapper;

    @Autowired
    public UserMapper userMapper;

    @Override
    public Integer setVisible() {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        return evaluateMapper.setVisible(newEpoch,ProcessType.ResultConsultation.getCode());
    }

    @Override
    public Integer findNewEpoch() {
        return evaluateMapper.findNewEpoch();
    }

    @Override
    public Integer findNewEnableProcess() {
        return evaluateMapper.findNewestEnableProcess();
    }

    @Override
    @Transactional
    public List<AllStaff> findAll() {
        Integer userId = AuthenticationUtil.getUserId();
        String department = evaluateMapper.findDepartmentById(userId);
        List<AllStaff> allData = evaluateMapper.findAll(department);
        return allData;
    }

    @Override
    public List<EvaluateProcess> findEvaluateStep() {
        Integer newEpoch = evaluateMapper.findNewEpoch();
        return evaluateMapper.findEvaluateStep(newEpoch);
    }

    @Override
    @Transactional
    public Integer evaluateTick(EvaluateTickSetVO evaluateTickSetVO) {

        Long todoListId = evaluateTickSetVO.getTodoListId();
        List<Double> tickSet = evaluateTickSetVO.getTickSet();

        Integer operation = todoListMapper.findTodoListById(todoListId);
        if(operation.equals(OperationType.FINISHED.getCode())){
            return 1;
        }

        if(tickSet == null || tickSet.size() != Guideline.values().length){
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "打分数据提交有误!");
        }
        for (Guideline guideline : Guideline.values()){
            int dimensionId = guideline.getCode();
            Score score = new Score();
            score.setScore(tickSet.get(dimensionId - 1));
            score.setDimensionId(dimensionId);
            score.setTodoId(todoListId);

            evaluateMapper.addScore(score);
        }
        //修改操作状态和时间
        todoListMapper.updateOperationAndCompleteTime(todoListId,OperationType.FINISHED.getCode(), LocalDateTime.now());
        return 1;
    }

    @Override
    @Transactional
    public Integer editEvaluateStep(ProcessStepVO processStepVO) {
        Integer newEpoch = evaluateMapper.findNewEpoch();

        if(processStepVO.getIsNew()){
            //1.先将之前的环节的enable设置为0
            evaluateMapper.setEnableBefore(0);
            //2.删除自主评估人
            relationshipMapper.deleteSelfRelationship();
            //3.删除enable=0的评估关系
            relationshipMapper.deleteEvaluationMatrixEnableFalse();
            //4.更新关系矩阵的epoch,令其+1
            relationshipMapper.addRelationshipEpoch();
            //5.将所有用户的置信度重置为1
            userMapper.setWeight(1.0);
            if(newEpoch == null){
                newEpoch = 0;
            }
            newEpoch = newEpoch + 1;
            List<StepTimeVO> stepTimeVOList = processStepVO.getStepTimeVOList();
            for (int index = 1; index <= stepTimeVOList.size(); index++){
                StepTimeVO stepTimeVO = stepTimeVOList.get(index - 1);
                EvaluateProcess evaluateProcess = new EvaluateProcess();
                evaluateProcess.setEpoch(newEpoch);
                evaluateProcess.setEvaluateStep(index);
                String description = ProcessType.getDescriptionByCode(index);
                evaluateProcess.setDescription(description);
                evaluateProcess.setExtra(0);
                evaluateProcess.setStartDate(stepTimeVO.getStartDate());
                evaluateProcess.setEndDate(stepTimeVO.getEndDate());
                if(index == 1){
                    evaluateProcess.setEnable(1);
                }else {
                    evaluateProcess.setEnable(0);
                }
                evaluateMapper.addNewEvaluateStep(evaluateProcess);
            }
            //修改标题
            evaluateMapper.updateTitle(processStepVO.getTitle());

        }else {
            List<StepTimeVO> stepTimeVOList = processStepVO.getStepTimeVOList();
            for (int index = 1; index <= stepTimeVOList.size(); index++){
                StepTimeVO stepTimeVO = stepTimeVOList.get(index - 1);
                EvaluateProcess evaluateProcess = new EvaluateProcess();
                evaluateProcess.setEpoch(newEpoch);
                evaluateProcess.setEvaluateStep(index);
                evaluateProcess.setStartDate(stepTimeVO.getStartDate());
                evaluateProcess.setEndDate(stepTimeVO.getEndDate());
                evaluateMapper.updateEvaluateStep(evaluateProcess);
            }
        }
        return 1;
    }

    @Override
    public EvaluateTable evaluateTableInfo() {
        return evaluateMapper.evaluateTableInfo();
    }

    @Override
    public Integer updateTableInfo(EvaluateTable evaluateTable) {
        return evaluateMapper.updateTableInfo(evaluateTable);
    }

    @Override
    public List<SelfEvaluatorVO> findSelfEvaluator(Integer userId) {
        return evaluateMapper.findSelfEvaluator(userId);
    }

    @Override
    public List<SelfEvaluatedVO> findSelfEvaluated(Integer userId) {
        return evaluateMapper.findSelfEvaluated(userId);
    }
}
