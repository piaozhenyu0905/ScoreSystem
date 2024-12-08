package com.system.assessment.vo;

import lombok.Data;

@Data
public class GetByStyleCondition {
    public String condition; //lxyz 或 业务

    public Integer content; //lxyz:IP +LP群体（IP+LP）(0)，X+Y群体（中坚+精英）(1)，Z群体（成长）(2)
                            //取值为研发(0)和非研发(1)
}
