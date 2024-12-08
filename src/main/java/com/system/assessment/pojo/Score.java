package com.system.assessment.pojo;

import lombok.Data;

@Data
public class Score {
    public Long id;

    public Integer dimensionId;

    public Double score;

    public String description;

    public Double weight;

    public Long todoId;
}
