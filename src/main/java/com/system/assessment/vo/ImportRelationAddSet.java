package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class ImportRelationAddSet {
    public Integer evaluatedId;

    public List<Integer> evaluatorId;
}
