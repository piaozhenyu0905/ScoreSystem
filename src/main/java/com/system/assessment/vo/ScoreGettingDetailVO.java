package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class ScoreGettingDetailVO {
    public  Integer evaluatedId;

    private String department; //评估者的部门

    private String lxyz;

    private String business;

    private String evaluatedName;   // 评估者的名字

    private List<SingleScoreWithSupervisor> singleScore; //单次打分
}
