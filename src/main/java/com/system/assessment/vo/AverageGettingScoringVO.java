package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class AverageGettingScoringVO {

    public Integer userId;

    public String evaluatedName;

    public String department;

    public List<Double> scoreList;

    public Double totalScore;

    public String lxyz;

    public String business;
}
