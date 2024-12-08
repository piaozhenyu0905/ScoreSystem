package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class AverageScoreByCondition {
    public List<Double> scoreList;

    public Double totalScore;

    public String lxyz;

    public String business;
}
