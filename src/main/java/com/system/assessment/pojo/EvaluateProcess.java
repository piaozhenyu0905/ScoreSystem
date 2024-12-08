package com.system.assessment.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EvaluateProcess {
    public Long id;

    public String description;

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public LocalDate startDate;

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public LocalDate endDate;

    public Integer epoch;

    public Integer evaluateStep;

    public Integer enable;

    public Integer extra;
}
