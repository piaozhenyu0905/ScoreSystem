package com.system.assessment.vo;

import lombok.Data;

@Data
public class RelationshipCheckVO {
    public String evaluatorEmail;

    public String evaluatedName;

    public String evaluatedWorkNum;

    public String evaluatorName;

    public String evaluatorWorkNum;

    public Integer evaluatedId;

    public Integer evaluatorId;
}
