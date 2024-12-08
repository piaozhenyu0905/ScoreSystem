package com.system.assessment.pojo;

import lombok.Data;

@Data
public class EvaluateRelationship {
    public Long id;

    public Integer evaluatedUser;

    public Integer evaluator;

    public String evaluateType;

    public Integer epoch;

    public Integer enable;
}
