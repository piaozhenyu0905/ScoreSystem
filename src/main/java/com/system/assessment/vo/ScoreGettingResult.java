package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoreGettingResult {
    private String department; //被评估者的部门

    private String state;  //状态

    private String evaluatedName;   // 被评估者的名字

    private String evaluatorName;   // 评估者的名字

    @JsonIgnore
    private List<ScoreSimple> temScores;    // 分数列表

    private List<Double> scores;

    public void postHandle(){
        if(temScores != null && temScores.size() != 0){
            scores = new ArrayList<>();
            temScores.forEach(scoreSimple->{
                scores.add(scoreSimple.getScore());
            });

        }
    }
}
