package com.system.assessment.vo;

import lombok.Data;

@Data
public class SelfEvaluatorVO {
    public String username; //工号

    public String name; //姓名

    public String department;// 部门

    public String type; //标签

    public Integer evaluator;
}