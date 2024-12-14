package com.system.assessment.vo;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
public class FixEvaluatedAddingVO {
    public Integer userId;

    public List<Integer> evaluatorIds;
}
