package com.system.assessment.vo;

import lombok.Data;

@Data
public class TaskEvaluateInfo {
    public String evaluatorName;

    public String evaluatedName;

    public Integer evaluatorId;

    public Integer evaluatedId;

    public Double confidenceLevel;
}
