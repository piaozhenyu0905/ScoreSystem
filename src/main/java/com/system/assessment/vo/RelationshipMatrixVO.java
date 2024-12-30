package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class RelationshipMatrixVO {

    public Integer userId;

    public String name;

    public String supervisorName1;

    public String supervisorName2;

    public String supervisorName3;

    public Double weight1;

    public Double weight2;

    public Double weight3;

    public String department;

    public String lxyz;

    public String business;

    public String firstAdminName;

    public String secondAdminName;

    public String superAdminName;

    public String hrName;

    public String relatedPerson; //相关人

}
