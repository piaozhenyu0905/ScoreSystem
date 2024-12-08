package com.system.assessment.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class EvaluationRelationshipVO {
    public Integer pageSize;

    public Integer pageNum;

    public String name;
}
