package com.system.assessment.vo;

import lombok.Data;

import java.util.Date;

@Data
public class SessionVO {
    private Integer code;
    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */

    private String msg;
    /**
     * 查询到的结果数据，
     */

    private Date timeStamp;

    public Boolean isFirstLogin;

    private Integer role;

}
