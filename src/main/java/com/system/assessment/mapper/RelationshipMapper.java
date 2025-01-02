package com.system.assessment.mapper;

import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateRelationship;
import com.system.assessment.pojo.User;
import com.system.assessment.vo.AssessorVO;
import com.system.assessment.vo.RelatedPersonInfoVO;
import com.system.assessment.vo.RelationshipCheckVO;
import com.system.assessment.vo.RelationshipEvaluatorInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RelationshipMapper {
    public List<AssessorVO> findAssessor(@Param("id")Integer id);

    public List<RelatedPersonInfoVO> findRelatedPersonInfo(@Param("id")Integer userId);

    @Select("select id from user where name = #{name} and work_num = #{workNum} and is_delete = 0")
    public Integer judgeExistByNameAndWorkNum(@Param("name") String name,
                                              @Param("workNum")String workNum);

    public Integer findSingleRelationship(@Param("evaluatorId") Integer evaluatorId,
                                          @Param("evaluatedId") Integer evaluatedId);


    public List<Integer> findAllEvaluated();

    public List<RelationshipCheckVO> findAllRelationship();

    public Integer deleteFixedRelationship();

    public Integer deleteSelfRelationship();

    public Integer deleteAllRelationshipByEvaluatedId(@Param("id")Integer id);

    public Integer deleteRelationshipById(@Param("id")Integer userId);

    public List<RelationshipCheckVO> findRelationshipById(@Param("id")Integer userId);

    public Integer deleteEvaluationMatrix(@Param("userId")Integer userId,
                                          @Param("evaluatorId")Integer evaluatorId,
                                          @Param("type")Integer type);

    public Integer deleteEvaluation(@Param("userId")Integer userId,
                                          @Param("evaluatorId")Integer evaluatorId,
                                          @Param("type")Integer type);

    public Integer deleteEvaluationMatrixEnableFalse();

    public List<RelationshipEvaluatorInfo> findEvaluatorById(@Param("id")Integer id,
                                                             @Param("type")Integer type);

    public Integer addRelationshipEpoch();

    public List<User> findAllUser(@Param("name") String name);

    public Integer addRelationship(@Param("relationship") EvaluateRelationship evaluateRelationship);
}
