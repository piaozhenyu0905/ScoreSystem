package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class RejectVO {
    public List<Long> tasksToReject;

    public List<String> reasonsToReject;
}
