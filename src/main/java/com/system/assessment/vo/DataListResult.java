package com.system.assessment.vo;

import lombok.Data;

import java.util.List;

@Data
public class DataListResult<T> {

    public long total;

    public List<T> data;

    public DataListResult(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public DataListResult() {

    }
}
