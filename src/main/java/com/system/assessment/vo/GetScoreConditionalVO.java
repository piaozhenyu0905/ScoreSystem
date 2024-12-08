package com.system.assessment.vo;

import lombok.Data;

@Data
public class GetScoreConditionalVO {
    public Integer pageSize;

    public Integer pageNum;

    public String name; //被评估人

    public String department;

    public String condition;

    public Integer content;

    public Integer orderAt; //排序的指标下表1-9 总分是0
}
