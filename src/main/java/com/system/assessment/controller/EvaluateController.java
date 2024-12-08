package com.system.assessment.controller;

import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.EvaluateProcess;
import com.system.assessment.pojo.EvaluateTable;
import com.system.assessment.pojo.User;
import com.system.assessment.service.EvaluateService;
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
@Api(tags = "评估管理")
public class EvaluateController {

    @Autowired
    public EvaluateService evaluateService;

    @ApiOperation("返回成长评估表的卷首")
    @RequestMapping(value = "/evaluate/table",method = RequestMethod.GET)
    public ResponseResult evaluateTableInfo(){
        EvaluateTable evaluateTable = evaluateService.evaluateTableInfo();
        return ResponseResult.success(evaluateTable);
    }

    @ApiOperation("修改成长评估表的卷首")
    @RequestMapping(value = "/evaluate/table/update",method = RequestMethod.POST)
    public ResponseResult updateTableInfo(@RequestBody EvaluateTable evaluateTable){
        Integer result = evaluateService.updateTableInfo(evaluateTable);
        return ResponseResult.success();
    }


    @ApiOperation("维护评估关系-我的评估人")
    @RequestMapping(value = "/evaluate/selfEvaluator",method = RequestMethod.GET)
    public ResponseResult findSelfEvaluator(){
        Integer userId = AuthenticationUtil.getUserId();
        List<SelfEvaluatorVO> selfEvaluator = evaluateService.findSelfEvaluator(userId);
        return ResponseResult.success(selfEvaluator);
    }

    @ApiOperation("维护评估关系-我要评估")
    @RequestMapping(value = "/evaluate/selfEvaluated",method = RequestMethod.GET)
    public ResponseResult findSelfEvaluated(){
        Integer userId = AuthenticationUtil.getUserId();
        List<SelfEvaluatedVO> selfEvaluator = evaluateService.findSelfEvaluated(userId);
        return ResponseResult.success(selfEvaluator);
    }


    @ApiOperation("获取所有人")
    @RequestMapping(value = "/findAll",method = RequestMethod.GET)
    //已验证
    public ResponseResult findAll(){
        List<AllStaff> allData = evaluateService.findAll();
        return ResponseResult.success(allData);
    }

}
