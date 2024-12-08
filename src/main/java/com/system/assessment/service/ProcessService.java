package com.system.assessment.service;

import org.apache.ibatis.annotations.Param;

public interface ProcessService {

    public Integer findExtra(Integer epoch);

    public void gotoNewStep(Integer newEpoch);

    public void StatisticsOver();

    public void ScoringOver();

    public void RelationConfirmedPublic();
}
