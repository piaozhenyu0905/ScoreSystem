package com.system.assessment.vo;

import com.system.assessment.pojo.EvaluateProcess;
import lombok.Data;

import java.util.List;

@Data
public class EvaluateProcessVO {
    public Integer processingNum;

    public List<EvaluateProcess> evaluateStep;
}
