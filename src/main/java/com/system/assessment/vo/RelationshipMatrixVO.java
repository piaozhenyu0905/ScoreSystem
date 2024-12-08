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

    public String supervisorName4;

    public List<RelationshipEvaluatorInfo> relationshipFixedList; //固定评估人

    public List<RelationshipEvaluatorInfo> relationshipSelfList; //自主评估人
}
