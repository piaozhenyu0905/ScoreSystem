package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class ScoreDetailVO {
    public  Integer userId;

    private String department; //评估者的部门

    private Double confidenceLevel; //置信度

    private String evaluatorName;   // 评估者的名字

    private List<SingleScore> singleScore; //单次打分
}
