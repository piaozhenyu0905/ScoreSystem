package com.system.assessment.service.Impl;

import com.github.pagehelper.PageHelper;
import com.system.assessment.constants.OperationType;
import com.system.assessment.mapper.EvaluateMapper;
import com.system.assessment.mapper.TodoListMapper;
import com.system.assessment.mapper.UserMapper;
import com.system.assessment.pojo.TodoList;
import com.system.assessment.service.EmailService;
import com.system.assessment.service.TodoService;
import com.system.assessment.vo.AssessmentHistoryVO;
import com.system.assessment.vo.ScoreResult;
import com.system.assessment.vo.TodoListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public EvaluateMapper evaluateMapper;

    @Autowired
    public TodoListMapper todoListMapper;

    @Autowired
    public EmailService emailService;

    @Autowired
    @Lazy
    public AsyncTask asyncTask;

    @Override
    public void noticeAllNotCompleted() {
        asyncTask.noticeAllNotCompleted();
    }

    @Override
    public Boolean isValued(long todoListId) {
        Integer operation = todoListMapper.isValued(todoListId);
        return  operation != OperationType.INVALUED.getCode();
    }

    @Override
    public List<AssessmentHistoryVO> findHistory(Integer userId) {
        List<AssessmentHistoryVO> histories = todoListMapper.findHistory(userId);
        histories.forEach(history->{
            history.postHandle();
        });
        return histories;
    }

    @Override
    public ScoreResult findScoreResult(Long taksId) {
        ScoreResult scoreResult = todoListMapper.findScoreResult(taksId);
        if(scoreResult != null){
            scoreResult.postHandle();
        }
        return scoreResult;
    }

    @Override
    public Integer addTodoList(TodoList todoList) {
        return todoListMapper.addTodoList(todoList);
    }

    public List<TodoListVO> findTodoList(Integer userId, Integer pageNum, Integer pageSize){
        //分页设置
        Integer newEpoch = evaluateMapper.findNewEpoch();
        PageHelper.startPage(pageNum, pageSize);
        return todoListMapper.findTodoList(userId, newEpoch);
    };
}
