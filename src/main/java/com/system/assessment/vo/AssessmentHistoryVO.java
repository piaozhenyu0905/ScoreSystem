package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.system.assessment.constants.ScoringOperationType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AssessmentHistoryVO {
    @JsonIgnore
    public Integer enable;

    public Integer assessorId;

    public String assessorName;

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public LocalDate assessorTime;

    @JsonIgnore
    public Integer operation;

    public Boolean isReject;

    public Long taskId;

    public void postHandle(){
        if(enable.equals(ScoringOperationType.Rejected.getCode())){
            isReject = true;
        }else {
            isReject = false;
        }
    }
}
