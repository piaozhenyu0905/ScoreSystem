package com.system.assessment.controller;

import com.system.assessment.constants.ProcessType;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.service.*;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
@Api(tags = "得分评分看板")
public class ScoringBoardController {

    @Autowired
    public EvaluateService evaluateService;

    @Autowired
    public ProcessService processService;

    @Autowired
    public UserService userService;

    @Autowired
    public ScoringBoardService scoringBoardService;

    @ApiOperation("打分进度")
    @RequestMapping(value = "/score/process",method = RequestMethod.POST)
    public ResponseResult getScoreProcess(){

        ScoreProcessVO scoreProcess = scoringBoardService.getScoreProcess();
        return ResponseResult.success(scoreProcess);
    }


    @ApiOperation("评分看板-统计评估人群本轮次平均打分")
    @RequestMapping(value = "/average/score",method = RequestMethod.POST)
    public ResponseResult findAverageScoring(@RequestBody AverageScoringConditionVO averageScoringConditionVO){

        DataListResult averageScoring = scoringBoardService.findAverageScoring(averageScoringConditionVO);
        return ResponseResult.success(averageScoring);
    }

    @ApiOperation("评分看板-查看某人打分的详情")
    @RequestMapping(value = "/average/score/detail",method = RequestMethod.GET)
    public ResponseResult findAverageScoringDetail(@RequestParam("id") Integer userId,
                                                   @RequestParam("condition") Integer condition,
                                                   @RequestParam("orderBy")Integer orderBy){

        // condition:0 代表总分， 1-9代表对应的维度
        List<ScoreDetailIncludeEvaluated>  averageScoringDetail = scoringBoardService.findAverageScoringDetail(userId, condition, orderBy);
        return ResponseResult.success(averageScoringDetail);
    }

    @ApiOperation("评分看板-确认得分")
    @RequestMapping(value = "/confirm/score/single",method = RequestMethod.GET)
    public ResponseResult confirmScoreSingle(@RequestParam("id") Integer userId){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

        scoringBoardService.confirmScoreSingle(userId);
        return ResponseResult.success();
    }

    @ApiOperation("评分看板-批量确认得分")
    @RequestMapping(value = "/confirm/score/many",method = RequestMethod.POST)
    public ResponseResult confirmScoreMany(@RequestBody ConfirmManyVO confirmManyVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        scoringBoardService.confirmScoreMany(confirmManyVO);
        return ResponseResult.success();
    }

    @ApiOperation("评分看板-设置置信度")
    @RequestMapping(value = "/confidenceLevel/config",method = RequestMethod.POST)
    public ResponseResult confidenceLevel(@RequestBody confidenceLevelVO confidenceLevelVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        scoringBoardService.confidenceLevel(confidenceLevelVO);
        return ResponseResult.success();
    }

    @ApiOperation("评分看板-驳回")
    @RequestMapping(value = "/reject",method = RequestMethod.POST)
    public ResponseResult reject(@RequestBody RejectVO rejectVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        scoringBoardService.reject(rejectVO);
        return ResponseResult.success();
    }

    public Boolean isVisible(){
        Integer userId = AuthenticationUtil.getUserId();
        Integer role = userService.findRole(userId);
        Integer newEpoch = evaluateService.findNewEpoch();

        if(role != Role.superAdmin.getCode() && processService.findExtra(newEpoch) == 0){
            return false;
        }
        return true;
    }

    @ApiOperation("得分看板-设置得分系数")
    @RequestMapping(value = "/assessorConfidenceLevel",method = RequestMethod.POST)
    public ResponseResult assessorConfidenceLevel(@RequestBody AssessorConfidenceLevelVO assessorConfidenceLevelVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode())
                && !newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            return ResponseResult.success();
        }
        if(!isVisible()){
            return ResponseResult.success();
        }

        scoringBoardService.assessorConfidenceLevel(assessorConfidenceLevelVO);
        return ResponseResult.success();
    }

    @ApiOperation("得分看板-统计被评估人本轮次平均得分")
    @RequestMapping(value = "/getBoard",method = RequestMethod.POST)
    public ResponseResult getAverageScoreBoard(@RequestBody GetScoreConditionalVO getScoreConditionalVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode())
                && !newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            DataListResult<Object> objectDataListResult = new DataListResult<>();
            objectDataListResult.setTotal(0);
            objectDataListResult.setData(new ArrayList<>());
            return ResponseResult.success(objectDataListResult);
        }
        if(!isVisible()){
            DataListResult<Object> objectDataListResult = new DataListResult<>();
            objectDataListResult.setTotal(0);
            objectDataListResult.setData(new ArrayList<>());
            return ResponseResult.success(objectDataListResult);
        }

        DataListResult averageScoreBoard = scoringBoardService.getAverageScoreBoard(getScoreConditionalVO);
        return ResponseResult.success(averageScoreBoard);
    }

    @ApiOperation("得分看板-根据维度条件查询")
    @RequestMapping(value = "/getBoard/condition",method = RequestMethod.POST)
    public ResponseResult getAverageScoreBoardByCondition(@RequestBody GetByStyleCondition getScoreConditionalVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode())
                && !newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            return ResponseResult.success();
        }
        if(!isVisible()){
            return ResponseResult.success();
        }

        AverageScoreByCondition averageScoreBoardByCondition = scoringBoardService.getAverageScoreBoardByCondition(getScoreConditionalVO);
        return ResponseResult.success(averageScoreBoardByCondition);
    }

    @ApiOperation("得分看板-查询群体平均分")
    @RequestMapping(value = "/getBoard/conditionAll",method = RequestMethod.GET)
    public ResponseResult getAverageScoreBoardAll(@RequestParam("condition") String condition){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode())
                && !newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            return ResponseResult.success();
        }
        if(!isVisible()){
            return ResponseResult.success();
        }

        GroupAverageVO groupAverageVO = scoringBoardService.getAverageScoreBoardAll(condition);
        return ResponseResult.success(groupAverageVO);
    }


    @ApiOperation("得分看板-根据维度条件查询-修改平均分系数")
    @RequestMapping(value = "/getBoard/update",method = RequestMethod.POST)
    public ResponseResult updateAverageScoreBoardByCondition(@RequestBody UpdateAverageScoreByConditionalVO getScoreConditionalVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        scoringBoardService.updateAverageScoreBoardByCondition(getScoreConditionalVO);
        return ResponseResult.success();
    }

    @ApiOperation("得分看板-查看详情")
    @RequestMapping(value = "/getBoard/detail",method = RequestMethod.GET)
    public ResponseResult findAverageGettingDetail(@RequestParam("id") Integer userId){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode()) &&
                !newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            return ResponseResult.success();
        }
        if(!isVisible()){
            return ResponseResult.success();
        }

        List<ScoreGettingDetailIncludeEvaluated> averageGettingDetail = scoringBoardService.findAverageGettingDetail(userId);
        return ResponseResult.success(averageGettingDetail);
    }

    @ApiOperation("导出得分看板")
    @RequestMapping(value = "/export/getBoard",method = RequestMethod.GET)
    public void exportGetBoard(HttpServletResponse response){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        //处于第三、四环节，且处于可见情况下才能导出得分看板
        if(newEnableProcess.equals(ProcessType.ResultConsultation.getCode()) ||
                newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            if(isVisible()){
                // 设置文件下载响应头
                scoringBoardService.exportGetBoard(response);
            }
        }
    }

}
