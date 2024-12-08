package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class AddSelfEvaluatedVO {
    public Integer selfId;

    public List<Integer> userIds;
}
