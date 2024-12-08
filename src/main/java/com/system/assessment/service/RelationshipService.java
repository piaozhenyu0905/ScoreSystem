package com.system.assessment.service;

import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.vo.AddSelfEvaluatedVO;
import com.system.assessment.vo.DataListResult;
import com.system.assessment.vo.EvaluationRelationshipVO;
import com.system.assessment.vo.RelationshipMatrixVO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RelationshipService {

    public Integer addFixedRelationship(String evaluatorName, String evaluatedName);

    public Integer addRelationshipByFile(MultipartFile file);

    public List<Integer> findAllEvaluated();

    public Integer addEvaluationMatrix(Integer userId,Integer evaluatorId);

    public Integer deleteEvaluationMatrix(Integer userId,Integer evaluatorId);

    public DataListResult findEvaluationMatrix(EvaluationRelationshipVO evaluationRelationshipVO);

    public Integer addSelfEvaluated(AddSelfEvaluatedVO addSelfEvaluatedVO);

    public Integer addRelationship(EvaluateRelationship evaluateRelationship);
}
