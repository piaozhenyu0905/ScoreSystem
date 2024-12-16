package com.system.assessment.mapper;

import com.system.assessment.pojo.*;
import com.system.assessment.vo.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface EvaluateMapper {

    public Integer assessorConfidenceLevel(@Param("confidenceLevel")Double confidenceLevel,
                                           @Param("userId")Integer userId);

    public Integer setVisible(@Param("epoch")Integer epoch,
                              @Param("step")Integer step);

    public Integer findExtra(@Param("epoch")Integer epoch,
                             @Param("step")Integer step);

    public LocalDate findEndDate(@Param("epoch")Integer epoch);

    public Integer setEnableBefore(@Param("enable")Integer enable);

    public Integer gotoNewProcess(@Param("step")Integer step,
                                  @Param("epoch")Integer epoch);

    public Integer endOldProcess(@Param("step")Integer step,
                                 @Param("epoch")Integer epoch);

    public Integer findNewestEnableProcess();

    public EvaluateProcess findNewEvaluateProcess();

    public Integer updateAverageScore(@Param("type")String type,
                                      @Param("content")Integer content,
                                      @Param("epoch")Integer epoch,
                                      @Param("weight")Double weight);

    public Integer deleteAverageSum(@Param("type")String type,
                                    @Param("content")Integer content,
                                    @Param("epoch")Integer epoch);

    public AverageSum findAverageSum(@Param("type")String type,
                                     @Param("content")Integer content,
                                     @Param("epoch")Integer epoch);

    public Integer insertAverageSum(@Param("averageSum") AverageSumTable averageSum);

    public Integer addScore(@Param("score") Score score);

    @Select("select department from user where id = #{id}")
    public String findDepartmentById(@Param("id")Integer userId);

    public List<AllStaff> findAll(@Param("department")String department);

    public List<EvaluateProcess> findEvaluateStep(@Param("epoch")Integer epoch);

    public Integer updateEvaluateStep(@Param("evaluateProcess")EvaluateProcess evaluateProcess);

    public Integer addNewEvaluateStep(@Param("evaluateProcess")EvaluateProcess evaluateProcess);

    public Integer findNewEpoch();

    public Integer updateTableInfo(@Param("evaluateTable")EvaluateTable evaluateTable);

    public EvaluateTable evaluateTableInfo();

    public List<SelfEvaluatorVO> findSelfEvaluator(@Param("userId") Integer userId);

    public List<SelfEvaluatedVO> findSelfEvaluated(@Param("userId") Integer userId);

    public List<SelfEvaluatedVO> findSelfEvaluatedList(@Param("userId") Integer userId);


    public Integer findProcessId(@Param("epoch")Integer epoch, @Param("evaluateStep")Integer evaluateStep);
}
