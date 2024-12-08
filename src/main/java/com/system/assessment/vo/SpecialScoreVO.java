package com.system.assessment.vo;

import lombok.Data;

@Data
public class SpecialScoreVO {
    public Double weight;

    public Double score;

    public Boolean isSupervisor;

    public SpecialScoreVO(Boolean isSupervisor, Double score, Double weight) {
        this.score = score;
        this.isSupervisor = isSupervisor;
        this.weight = weight;
    }


}
