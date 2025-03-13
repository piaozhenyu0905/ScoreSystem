package com.system.assessment.service;

import com.system.assessment.pojo.TodoList;
import com.system.assessment.vo.AssessmentHistoryVO;
import com.system.assessment.vo.LastTickScoreList;
import com.system.assessment.vo.ScoreResult;
import com.system.assessment.vo.TodoListVO;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TodoService {

    public void exportEvaluatedInfo(HttpServletResponse response);

    public void exportTick(HttpServletResponse response);

    public LastTickScoreList findLastTick(Long taskId);

    public void noticeAllNotCompleted();

    public Boolean isValued(long todoListId);

    public List<AssessmentHistoryVO> findHistory(Integer userId);

    public ScoreResult findScoreResult(Long taskId);

    public Integer addTodoList(TodoList todoList);

    public List<TodoListVO> findTodoList(Integer userId, Integer pageNum, Integer pageSize);
}
