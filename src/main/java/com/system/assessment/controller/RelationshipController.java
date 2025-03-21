package com.system.assessment.controller;

import com.system.assessment.constants.ProcessType;
import com.system.assessment.constants.Role;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.pojo.User;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.RelationshipService;
import com.system.assessment.service.UserService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    public UserService userService;

    @ApiOperation("我要评估-添加用户自己的被评估人")
    @RequestMapping(value = "/add/evaluated",method = RequestMethod.POST)
    public ResponseResult addSelfEvaluated(@RequestBody AddSelfEvaluatedVO addSelfEvaluatedVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        Integer userId = AuthenticationUtil.getUserId();
        addSelfEvaluatedVO.setSelfId(userId);

        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            relationshipService.addSelfEvaluated(addSelfEvaluatedVO);
            return ResponseResult.success();
        }else if(newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            relationshipService.addSelfEvaluatedAtSecondStage(addSelfEvaluatedVO);
            return ResponseResult.success();
        }else {
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

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
        //第一阶段则直接删除关系
        if(newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            relationshipService.deleteEvaluationMatrix(userId, evaluatorId);
            return ResponseResult.success();
        }
        //第二阶段需要删除关系并将打分任务的operation标志置为“已删除”
        else if(newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            relationshipService.deleteEvaluationMatrixAtSecondStage(userId, evaluatorId);
            return ResponseResult.success();
        }
        //第四阶段对关系进行浅删除
        else if(newEnableProcess.equals(ProcessType.ResultPublic.getCode())){

            relationshipService.updateEvaluationMatrixEnable(userId, evaluatorId);
            return ResponseResult.success();
        }
        else {
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

    }



    @ApiOperation("维护评估矩阵-添加固定评估人")
    @RequestMapping(value = "/add/relationship",method = RequestMethod.POST)
    public ResponseResult addEvaluationMatrix(@RequestBody FixEvaluatedAddingVO fixEvaluatedAddingVO){
        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }

        Integer userId = fixEvaluatedAddingVO.getUserId();
        for (int index = 0; index < fixEvaluatedAddingVO.getEvaluatorIds().size(); index++){
            Integer evaluatorId = fixEvaluatedAddingVO.getEvaluatorIds().get(index);
            //1.前置判断，不能做不合法操作
            if(userId.equals(evaluatorId)){
                return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "操作失败!不能添加自己为评估人!");
            }
            Integer singleRelationship = relationshipService.findSingleRelationship(evaluatorId, userId);
            //2.判断选择的人是否已和选择的用户建立了评估关系
            if(singleRelationship != null && singleRelationship != 0){
                User basicInfoBySelfId = userService.findBasicInfoBySelfId(evaluatorId);
                String name = basicInfoBySelfId.getName();
                return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "操作失败!您当前选择的用户已经与"+name+"建立了评估关系!请重新选择!");
            }
        }

        if(newEnableProcess.equals(ProcessType.BuildRelationships.getCode())
                || newEnableProcess.equals(ProcessType.ResultPublic.getCode())){
            relationshipService.addEvaluationMatrix(fixEvaluatedAddingVO);
            return ResponseResult.success();
        }else if (newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            relationshipService.addEvaluationMatrixAtSecondStage(fixEvaluatedAddingVO);
            return ResponseResult.success();
        }
        else {
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

    }


    @ApiOperation("导入评估矩阵")
    @RequestMapping(value = "/import/relationship",method = RequestMethod.POST)
    public ResponseResult addRelationship(@RequestParam("file") MultipartFile file){

        Integer role = userService.findRole(AuthenticationUtil.getUserId());
        if(!role.equals(Role.superAdmin.getCode()) && !role.equals(Role.SecondAdmin.getCode())){
            return ResponseResult.error(401,"编辑失败,您不具有该权限!");
        }

        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        //只能在第一环节进行导入
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "该操作在当前环节无效!");
        }

        if (file.isEmpty()) {
            return ResponseResult.error(401, "该文件为空");
        }
        ImportINfo importINfo = relationshipService.addRelationshipExcel(file);
        if(importINfo == null){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "矩阵导入错误!");
        }else {
            if(importINfo.getErrorList() == null || importINfo.getErrorList().size() == 0){
                return ResponseResult.success();
            }else {
                String error = "导入失败的数据:"+ String.join("，", importINfo.getErrorList());
                return ResponseResult.error(401, error);
            }

        }
    }

    @ApiOperation("导出评估矩阵")
    @RequestMapping(value = "/export/relationship",method = RequestMethod.GET)
    public void exportExcel(HttpServletResponse response){
        // 设置文件下载响应头
        relationshipService.exportExcel(response);
    }

    @ApiOperation("返回固定评估人")
    @RequestMapping(value = "/matrix/relatedPersonInfo",method = RequestMethod.GET)
    public ResponseResult relatedPersonInfo(@RequestParam("userId") Integer userId){
        List<RelatedPersonInfoVO> relatedPersonInfoVOS = relationshipService.relatedPersonInfo(userId);
        return ResponseResult.success(relatedPersonInfoVOS);
    }

    @ApiOperation("返回自己要评估的评估人（自主评估）")
    @RequestMapping(value = "/find/assessor",method = RequestMethod.GET)
    public ResponseResult findAssessor(){
        Integer userId = AuthenticationUtil.getUserId();
        List<AssessorVO> assessorList = relationshipService.findAssessor(userId);
        return ResponseResult.success(assessorList);
    }
}
