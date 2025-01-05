package com.system.assessment.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.assessment.constants.Guideline;
import com.system.assessment.utils.MathUtils;
import com.system.assessment.vo.ScoreVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class AverageSum {
    public Long id;

    public Double weight; //平局分系数

    public Double totalScore;

    public String lxyz;

    public String business;

    @JsonIgnore
    public String type;

    @JsonIgnore
    public Integer content;

    @JsonIgnore
    public List<ScoreVO> averageList;

    public List<Double> scoreList;

    public void postHandle(){
        if(type.equals("lxyz")){
            if(content.equals(0)){
                lxyz = "IP+LP";
            }else if(content.equals(1)){
                lxyz = "中坚+精英";
            }else {
                lxyz = "成长";
            }
        }else if(type.equals("业务")){
            if(content.equals(0)){
                business = "研发";
            }else if(content.equals(1)){
                business = "非研发";
            }
        }
        scoreList = new ArrayList<>(Collections.nCopies(Guideline.values().length, 0.0));
        averageList.forEach(scoreVO -> {
            Integer dimensionId = scoreVO.getDimensionId();
            Double score = scoreVO.getScore();
            scoreList.set(dimensionId - 1, MathUtils.transformer(score * weight));
        });
    }
}
