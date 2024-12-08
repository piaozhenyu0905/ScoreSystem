package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class SingleScoreDetail {
    public Long todoId;

    public Integer enable;

    public Integer operation;

    public List<ScoreVO> scores;
}
