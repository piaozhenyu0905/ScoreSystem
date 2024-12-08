package com.system.assessment.vo;

import lombok.Data;

@Data
public class UserInfoSelectVO {

    public Integer pageSize;

    public Integer pageNum;

    public String username;

    public String workNum;

    public String department;

    public Integer role;
}
