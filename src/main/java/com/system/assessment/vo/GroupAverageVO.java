package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class GroupAverageVO {
    List<AverageScoreByCondition> dataList;
}
