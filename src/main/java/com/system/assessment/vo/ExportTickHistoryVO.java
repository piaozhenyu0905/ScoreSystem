package com.system.assessment.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ExportTickHistoryVO {
    public String evaluatedName;

    public String department;

    public LocalDateTime completeTime;

    public Double totalScore;

    public List<ScoreVO> scoreList;
}
