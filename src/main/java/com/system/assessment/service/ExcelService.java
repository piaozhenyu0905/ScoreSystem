package com.system.assessment.service;

import com.system.assessment.vo.NameAndWorkNum;
import com.system.assessment.vo.SupervisorVO;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Map;

public interface ExcelService {
    public void addUserExcel(Row row, Map<String, Integer> columnIndexMap, List<SupervisorVO> supervisorList);

    public void addRelationshipExcel(Row row, Map<String, Integer> columnIndexMap);

    public void relationshipExtract(NameAndWorkNum nameAndWorkNum, String content);

    public NameAndWorkNum extractCell(String content);

    public Boolean judgeUserExist(String name, String workNum);
}
