package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoreDetailIncludeEvaluated {

    private String department; //被评估者的部门

    private String evaluatedName;   // 被评估者的名字

    private List<Double> scores;

    @JsonIgnore
    private List<ScoreVO> singleScore; //单次打分

    private Double allScore; //总分

    public void postHandle(){
        if(singleScore != null && singleScore.size() != 0){
            scores = new ArrayList<>();
            singleScore.forEach(scoreSimple->{
                scores.add(scoreSimple.getScore());
            });

        }
    }
}
