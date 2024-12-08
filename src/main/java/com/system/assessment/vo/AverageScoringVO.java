package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class AverageScoringVO {

    public Integer userId;

    public String evaluatorName;

    public String department;

    public List<Double> scoreList;

    public Double confidenceLevel; //置信度

    public String state; //分数确认状态
}
