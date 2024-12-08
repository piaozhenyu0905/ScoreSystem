package com.system.assessment.vo;

import lombok.Data;

@Data
public class AverageScoringConditionVO {
    public Integer pageSize;

    public Integer pageNum;

    public String name;

    public String department;

    public String state;

    public Integer orderAt; //排序的指标下表1-9
}
