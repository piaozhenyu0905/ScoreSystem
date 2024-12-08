package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class PanelScoreBoardVO {
    public String lxyz;

    public String business;

    public Double totalScore;

    public Double lxyzTotalScore;

    public Double businessTotalScore;

    public List<Double> selfList;

    public List<Double> lxyzList;

    public List<Double> businessList;
}
