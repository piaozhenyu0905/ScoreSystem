package com.system.assessment.mapper;

import com.system.assessment.pojo.TodoList;
import com.system.assessment.pojo.User;
import com.system.assessment.vo.*;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TodoListMapper {
    public TaskEvaluateInfo findTaskEvaluateInfo(@Param("id")Long id);

    public Integer rejectSingle(@Param("reason")String reason,
                                 @Param("id")Long taskId,
                                @Param("enable")Integer enable);

    public List<Integer> findAllNotCompleted(@Param("epoch")Integer epoch);

    public Integer sumTotalPeople(@Param("epoch")Integer epoch);

    public Integer sumNotCompletedPeople(@Param("epoch")Integer epoch);

    public Integer setFinishedOperationToDeleted(@Param("evaluatorId")Integer evaluatorId,
                                         @Param("evaluatedId")Integer evaluatedId,
                                         @Param("operation")Integer operation,
                                         @Param("epoch")Integer epoch);

    public Integer setUnFinishedOperationToDeleted(@Param("evaluatorId")Integer evaluatorId,
                                                 @Param("evaluatedId")Integer evaluatedId,
                                                 @Param("operation")Integer operation,
                                                 @Param("epoch")Integer epoch);

    public Integer enableTaskById(@Param("epoch")Integer epoch,
                                  @Param("id")Integer id);

    public Integer deleteAverageTable(@Param("epoch")Integer epoch);

    public Integer findTodoListById(@Param("id") long id);

    public TodoListVO findTodoListIsExist(@Param("evaluatorId") Integer evaluatorId,
                                         @Param("evaluatedId")  Integer evaluatedId,
                                         @Param("epoch")Integer epoch);

    public Integer SetOperationInvalid(@Param("epoch")Integer epoch,
                                        @Param("operation")Integer operation);

    @Select("select operation from todo_list where id = #{id}")
    public Integer isValued(@Param("id")Long id);

    public Integer reject(@Param("id") Integer userId,
                          @Param("epoch") Integer epoch);

    public Integer confidenceLevel(@Param("condition") confidenceLevelVO confidenceLevelVO, @Param("epoch")Integer epoch);

    public Integer confirmScoreSingle(@Param("epoch") Integer epoch,
                                      @Param("userId") Integer userId);


    public ScoreGettingDetailVO findSingleGettingNewRound(@Param("epoch") Integer epoch,
                                                          @Param("id")Integer id);

    public  List<ScoreGettingDetailVO>  findAverageGettingNewRound(@Param("epoch") Integer epoch,
                                                            @Param("condition") GetScoreConditionalVO getScoreConditionalVO);

    public  List<ScoreGettingDetailVO>  findAverageGettingNewRoundByFirstAdmin(@Param("epoch") Integer epoch,
                                                                               @Param("condition") GetScoreConditionalVO getScoreConditionalVO,
                                                                               @Param("lxyz")String lxyz,
                                                                               @Param("id")Integer id);

    public  List<ScoreGettingDetailVO>  findAverageGettingNewRoundBySecondAdmin(@Param("epoch") Integer epoch,
                                                                               @Param("condition") GetScoreConditionalVO getScoreConditionalVO,
                                                                               @Param("lxyz")String lxyz,
                                                                               @Param("department")String department,
                                                                                @Param("id")Integer userId);

    public  List<ScoreGettingDetailVO>  findAverageGettingNewRoundByCondition(@Param("epoch") Integer epoch,
                                                                                @Param("type")String type,
                                                                                @Param("content")Integer department);


    public  List<ScoreDetailVO>  findScoringNewRoundIsNotEnable(@Param("epoch") Integer epoch);

    public  List<ScoreDetailVO>  findAverageScoringNewRound(@Param("epoch") Integer epoch,
                                                            @Param("condition") AverageScoringConditionVO averageScoringConditionVO);

    public  List<ScoreDetailIncludeEvaluated> findAverageScoringDetail(@Param("epoch") Integer epoch,
                                                                 @Param("id") Integer userId);

    public  List<ScoreGettingDetailIncludeEvaluated> findAverageGettingScoringDetail(@Param("epoch") Integer epoch,
                                                                       @Param("id") Integer userId);

    public SingleScore findSingleScoreByTodoId(@Param("id")Long id);

    public Integer updateOperationAndCompleteTime(@Param("taskId")Long taskId,
                                                  @Param("operation")Integer operation,
                                                  @Param("completeTime")LocalDateTime completeTime);

    public Integer updateOperation(@Param("taskId")Long taskId, @Param("operation")Integer operation);

    public List<AssessmentHistoryVO> findHistory(@Param("id") Integer userId);

    public ScoreResult findScoreResult(@Param("todoListId") Long taskId);

    public Integer addTodoList(@Param("todoList") TodoList todoList);

    public List<TodoListVO> findTodoList(@Param("userId") Integer userId,
                                         @Param("epoch")Integer epoch);
}
