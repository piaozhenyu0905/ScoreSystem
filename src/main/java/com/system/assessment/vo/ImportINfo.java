package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class ImportINfo {
    public Integer success;

    public List<String> errorList;
}
