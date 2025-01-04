package com.system.assessment.controller;


import com.system.assessment.constants.ProcessType;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.ProcessService;
import com.system.assessment.service.UserService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
@Api(tags = "流程相关")
public class ProcessController {

    @Autowired
    public EvaluateService evaluateService;

    @Autowired
    public ProcessService processService;


    @ApiOperation("设置可见")
    @RequestMapping(value = "/set/visible",method = RequestMethod.GET)
    //已验证
    public ResponseResult setVisible(){

        evaluateService.setVisible();
        return ResponseResult.success();
    }

    @ApiOperation("编辑/更新评估节点")
    @RequestMapping(value = "/edit/step",method = RequestMethod.POST)
    //已验证
    public ResponseResult editEvaluateStep(@RequestBody ProcessStepVO processStepVO){
        processStepVO.PreDo();
        List<StepTimeVO> stepTimeVOList = processStepVO.getStepTimeVOList();
        Boolean isValued = judgeIsValued(stepTimeVOList);

        if(!isValued){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "设置的日期有误，请重新设置!");
        }
        Integer result = evaluateService.editEvaluateStep(processStepVO);
        return ResponseResult.success();
    }

    public Boolean judgeIsValued(List<StepTimeVO> stepTimeVOList){
        StepTimeVO firstStepTimeVO = stepTimeVOList.get(0);
        StepTimeVO secondTimeVO = stepTimeVOList.get(1);
        StepTimeVO thirdTimeVO = stepTimeVOList.get(2);
        StepTimeVO fourthTimeVO = stepTimeVOList.get(3);
        if(firstStepTimeVO.getEndDate().isAfter(secondTimeVO.getEndDate()) ||
                secondTimeVO.getEndDate().isAfter(thirdTimeVO.getEndDate()) ||
                thirdTimeVO.getEndDate().isAfter(fourthTimeVO.getEndDate())){
            return false;
        }else {
            return true;
        }
    }

    @ApiOperation("查看最新的评估节点")
    @RequestMapping(value = "/find/step",method = RequestMethod.GET)
    //已验证
    public ResponseResult findEvaluateStep(){
        List<EvaluateProcess> evaluateStep = evaluateService.findEvaluateStep();
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        EvaluateProcessVO evaluateProcessVO = new EvaluateProcessVO();
        evaluateProcessVO.setEvaluateStep(evaluateStep);
        evaluateProcessVO.setProcessingNum(newEnableProcess);
        return ResponseResult.success(evaluateProcessVO);
    }

    //一.第一个环节，关系矩阵确立后，进行发布,并进入到第二个环节: 1.生成本轮所有的代办任务。2.邮件通知所有涉及的员工去完成
    @ApiOperation("流程推动-关系矩阵确认发布-进入下一步")
    @RequestMapping(value = "/relation/step",method = RequestMethod.GET)
    public ResponseResult RelationConfirmedPublic(){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        processService.RelationConfirmedPublic();
        return ResponseResult.success();
    }

    //二.评分环节，进入下一步第三环节: 1.本轮所有人不能再进行评议，也无法再进行确认（需要在之前的函数逻辑里进行补充），operation置为失效状态2。
    // 2 进行平均结果的统计，包括生成lxyz和业务类型的级联平均分，并加入到average_sum表
    @ApiOperation("流程推动-评分环节结束-进入下一步")
    @RequestMapping(value = "/score/step",method = RequestMethod.GET)
    public ResponseResult ScoringOver(){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        processService.ScoringOver();
        return ResponseResult.success();
    }

    //三.得分看板，1.结果发布，进入第四个环节，对所有员工发布邮件或短信提醒。可进行雷达图的查看（默认持续到下一轮环节设置开始前）。
    @ApiOperation("流程推动-得分统计结束-进入下一步")
    @RequestMapping(value = "/statistics/step",method = RequestMethod.GET)
    public ResponseResult StatisticsOver(){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.ResultConsultation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        processService.StatisticsOver();
        return ResponseResult.success();
    }

    //四.(不需要代码实现)结果公布后需要有一个时间点（截至时间结束后）将当前所有的自由评议人的关系删去。（或是下一轮关系矩阵确认时删去，需要考虑一下！！！！！！！！！！！！！）

}
