package com.system.assessment.vo;

import lombok.Data;

import java.util.Set;

@Data
public class ImportVO {
    public Set<String> notImportSet;

    public Set<String> notCompletedSet;
}
