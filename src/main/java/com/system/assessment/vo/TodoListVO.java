package com.system.assessment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TodoListVO {
    public Long id;

    public String type;

    public String detail;

    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    public LocalDate presentDate;

    public Integer operation; //操作（0：未完成评议; 1：已完成评议; 2:被驳回）

    public String evaluatedName;

    public String department;
}
