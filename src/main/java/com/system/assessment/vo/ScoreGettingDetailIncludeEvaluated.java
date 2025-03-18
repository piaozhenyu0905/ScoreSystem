package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoreGettingDetailIncludeEvaluated {

    private String department; //被评估者的部门

    private String type; //标签

    private String evaluatorName;   // 被评估者的名字

    private List<Double> scores;

    @JsonIgnore
    private List<ScoreVO> singleScore; //单次打分

    private Double confidenceLevel; //评估者对被评估者的打分置信度

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
