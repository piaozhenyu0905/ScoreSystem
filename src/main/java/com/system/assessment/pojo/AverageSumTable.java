package com.system.assessment.pojo;

import lombok.Data;

@Data
public class AverageSumTable {
    public Integer dimensionId;

    public Double averageScore;

    public Double weight;

    public String type;

    public Integer epoch;

    public Integer content;
}
