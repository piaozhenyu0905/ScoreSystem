package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class EvaluateTickSetVO {
    public Long todoListId;

    public List<Double> tickSet;
}
