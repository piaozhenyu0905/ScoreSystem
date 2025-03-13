package com.system.assessment.controller;

import com.github.pagehelper.PageInfo;
import com.system.assessment.constants.ProcessType;
import com.system.assessment.exception.CustomExceptionType;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.pojo.TodoList;
import com.system.assessment.pojo.User;
import com.system.assessment.service.EvaluateService;
import com.system.assessment.service.TodoService;
import com.system.assessment.utils.AuthenticationUtil;
import com.system.assessment.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api")  // 统一为所有方法设置路径前缀
@Slf4j
@Api(tags = "个人待办")
public class TodoController {

    @Autowired
    public TodoService todoService;

    @Autowired
    public EvaluateService evaluateService;

    @ApiOperation("一键提醒未打完分的人")
    @RequestMapping(value = "/notice",method = RequestMethod.GET)
    //已验证
    public ResponseResult noticeAllNotCompleted(){

        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "未处于打分环节!");
        }
        todoService.noticeAllNotCompleted();
        return ResponseResult.success();
    }


    @ApiOperation("我的代办-打分")
    @RequestMapping(value = "/todolist/tick",method = RequestMethod.POST)
    //已验证
    public ResponseResult evalulateTick(@RequestBody EvaluateTickSetVO evaluateTickSetVO){

        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 0;
        }
        if(!newEnableProcess.equals(ProcessType.Evaluation.getCode())){
            return ResponseResult.error(CustomExceptionType.USER_INPUT_ERROR.getCode(), "未处于打分环节!");
        }
        Integer result = evaluateService.evaluateTick(evaluateTickSetVO);
        return ResponseResult.success();
    }

    @ApiOperation("查看某次打分结果")
    @RequestMapping(value = "/find/score",method = RequestMethod.GET)
    //已验证
    public ResponseResult findScoreResult(@RequestParam("taskId") Long taskId){

        ScoreResult scoreResult = todoService.findScoreResult(taskId);
        return ResponseResult.success(scoreResult);
    }

    @ApiOperation("查看历史记录")
    @RequestMapping(value = "/find/history",method = RequestMethod.GET)
    //已验证
    public ResponseResult findHistory(){
        Integer userId = AuthenticationUtil.getUserId();
        List<AssessmentHistoryVO> history = todoService.findHistory(userId);
        // 3. 获取分页信息
        long total;
        if(history == null || history.size() ==0){
            total = 0;
        }else {
            total = history.size();
        }
        DataListResult<AssessmentHistoryVO> historyDataList = new DataListResult<>(total, history);
        return ResponseResult.success(historyDataList);
    }

    @ApiOperation("根据问卷返回上一次打分情况")
    @RequestMapping(value = "/find/lastTick",method = RequestMethod.GET)
    public ResponseResult findLastTick(@RequestParam("taskId") Long taskId){

        LastTickScoreList lastTickScoreList = todoService.findLastTick(taskId);
        return ResponseResult.success(lastTickScoreList);
    }



    @ApiOperation("个人代办-查看个人信息")
    @RequestMapping(value = "/todolist",method = RequestMethod.GET)
    public ResponseResult findTodoList(@RequestParam("pageSize") Integer pageSize,
                                       @RequestParam("pageNum") Integer pageNum){
        Integer userId = AuthenticationUtil.getUserId();
        List<TodoListVO> todoList = todoService.findTodoList(userId, pageNum, pageSize);
        PageInfo<TodoListVO> pageInfo = new PageInfo<>(todoList);

        // 获取总记录数
        long total = pageInfo.getTotal();
        DataListResult<TodoListVO> historyDataList = new DataListResult<>(total, todoList);
        return ResponseResult.success(historyDataList);
    }

    @ApiOperation("导出打分情况")
    @RequestMapping(value = "/export/tick",method = RequestMethod.GET)
    public void exportTick(HttpServletResponse response){

        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            // 设置文件下载响应头
            todoService.exportTick(response);
        }

    }

    @ApiOperation("导出待评分列表")
    @RequestMapping(value = "/export/evaluatedInfo",method = RequestMethod.GET)
    public void exportEvaluatedInfo(HttpServletResponse response){

        Integer newEnableProcess = evaluateService.findNewEnableProcess();
        if(newEnableProcess == null){
            newEnableProcess = 1;
        }
        if(!newEnableProcess.equals(ProcessType.BuildRelationships.getCode())){
            // 设置文件下载响应头
            todoService.exportEvaluatedInfo(response);
        }

    }


}
