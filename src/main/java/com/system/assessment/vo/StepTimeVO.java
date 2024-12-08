package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StepTimeVO {

    public String description;

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public LocalDate startDate;

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public LocalDate endDate;
}
