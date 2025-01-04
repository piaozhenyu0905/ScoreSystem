package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class NotCompletedSet {
    public List<EmailVO> evaluators; //评估人

    public Integer hrId; //本人所属的hrId

    public String hrEmail;

    public String hrName;
}
