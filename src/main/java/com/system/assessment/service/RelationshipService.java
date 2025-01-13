package com.system.assessment.service;

import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.vo.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface RelationshipService {
    public Integer addNewTask(Integer evaluatorId, Integer evaluatedId, Integer epoch);

    public List<AssessorVO> findAssessor(Integer userId);

    public List<RelatedPersonInfoVO> relatedPersonInfo(Integer userId);

    public void exportExcel(HttpServletResponse response);

    public ImportINfo addRelationshipExcel(MultipartFile file);

    public Integer findSingleRelationship(Integer evaluatorId, Integer evaluatedId);

    public Integer addFixedRelationshipById(Integer evaluatorId, Integer evaluatedId);

    public Integer addFixedRelationship(String evaluatorName, String evaluatedName);

    public Integer addRelationshipByFile(MultipartFile file);

    public List<Integer> findAllEvaluated();

    public Integer addEvaluationMatrixAtSecondStage(FixEvaluatedAddingVO fixEvaluatedAddingVO);

    public Integer addEvaluationMatrix(FixEvaluatedAddingVO fixEvaluatedAddingVO);

    public Integer addEvaluationMatrix(Integer userId,Integer evaluatorId);

    public Integer deleteEvaluationMatrix(Integer userId,Integer evaluatorId);

    public Integer updateEvaluationMatrixEnable(Integer userId,Integer evaluatorId);

    public Integer deleteEvaluationMatrixAtSecondStage(Integer userId,Integer evaluatorId);

    public DataListResult findEvaluationMatrix(EvaluationRelationshipVO evaluationRelationshipVO);

    public Integer addSelfEvaluated(AddSelfEvaluatedVO addSelfEvaluatedVO);

    public Integer addSelfEvaluatedAtSecondStage(AddSelfEvaluatedVO addSelfEvaluatedVO);

    public Integer addRelationship(EvaluateRelationship evaluateRelationship);
}
