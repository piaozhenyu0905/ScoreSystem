package com.system.assessment.service;

import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.vo.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RelationshipService {

    public Integer findSingleRelationship(Integer evaluatorId, Integer evaluatedId);

    public Integer addFixedRelationship(String evaluatorName, String evaluatedName);

    public Integer addRelationshipByFile(MultipartFile file);

    public List<Integer> findAllEvaluated();

    public Integer addEvaluationMatrixAtSecondStage(FixEvaluatedAddingVO fixEvaluatedAddingVO);

    public Integer addEvaluationMatrix(FixEvaluatedAddingVO fixEvaluatedAddingVO);

    public Integer addEvaluationMatrix(Integer userId,Integer evaluatorId);

    public Integer deleteEvaluationMatrix(Integer userId,Integer evaluatorId);

    public Integer deleteEvaluationMatrixAtSecondStage(Integer userId,Integer evaluatorId);

    public DataListResult findEvaluationMatrix(EvaluationRelationshipVO evaluationRelationshipVO);

    public Integer addSelfEvaluated(AddSelfEvaluatedVO addSelfEvaluatedVO);

    public Integer addSelfEvaluatedAtSecondStage(AddSelfEvaluatedVO addSelfEvaluatedVO);

    public Integer addRelationship(EvaluateRelationship evaluateRelationship);
}
