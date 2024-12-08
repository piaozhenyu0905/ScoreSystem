package com.system.assessment.controller;

import com.system.assessment.constants.ProcessType;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.RelationshipService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.AddSelfEvaluatedVO;
import com.system.assessment.vo.DataListResult;
import com.system.assessment.vo.EvaluationRelationshipVO;
import com.system.assessment.vo.RelationshipMatrixVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
@Api(tags = "关系矩阵管理")
public class RelationshipController {

    @Autowired
    public EvaluateService evaluateService;

    @Autowired
    public RelationshipService relationshipService;

    @ApiOperation("我要评估-添加用户自己的被评估人")
    @RequestMapping(value = "/add/evaluated",method = RequestMethod.POST)
    public ResponseResult addSelfEvaluated(@RequestBody AddSelfEvaluatedVO addSelfEvaluatedVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        Integer userId = AuthenticationUtil.getUserId();
        addSelfEvaluatedVO.setSelfId(userId);
        Integer result = relationshipService.addSelfEvaluated(addSelfEvaluatedVO);
        return ResponseResult.success(result);
    }

    @ApiOperation("维护评估矩阵-查看评估矩阵")
    @RequestMapping(value = "/find/relationships",method = RequestMethod.POST)
    public ResponseResult findEvaluationMatrix(@RequestBody EvaluationRelationshipVO evaluationRelationshipVO){
        DataListResult evaluationMatrix = relationshipService.findEvaluationMatrix(evaluationRelationshipVO);
        return ResponseResult.success(evaluationMatrix);
    }

    @ApiOperation("维护评估矩阵-删除固定评估人")
    @RequestMapping(value = "/update/relationship",method = RequestMethod.DELETE)
    public ResponseResult updateEvaluationMatrix(@RequestParam("userId")Integer userId,
                                                 @RequestParam("evaluatorId")Integer evaluatorId){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }
        relationshipService.deleteEvaluationMatrix(userId, evaluatorId);
        return ResponseResult.success();
    }

    @ApiOperation("维护评估矩阵-添加固定评估人")
    @RequestMapping(value = "/add/relationship",method = RequestMethod.POST)
    public ResponseResult addEvaluationMatrix(@RequestParam("userId")Integer userId,
                                                 @RequestParam("evaluatorId")Integer evaluatorId){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

        if(userId.equals(evaluatorId)){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "不能添加自己为评估人!");
        }

        relationshipService.addEvaluationMatrix(userId, evaluatorId);
        return ResponseResult.success();
    }

    @ApiOperation("导入评估矩阵")
    @RequestMapping(value = "/import/relationship",method = RequestMethod.POST)
    public ResponseResult addRelationship(@RequestParam("file") MultipartFile file){

        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

        if (file.isEmpty()) {
            return ResponseResult.error(401, "该文件为空");
        }
        Integer result = relationshipService.addRelationshipByFile(file);
        return ResponseResult.success();
    }

}
