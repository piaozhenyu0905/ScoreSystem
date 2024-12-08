package com.system.assessment.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TodoList {
    public Long id;

    public Integer ownerId;

    public String type;

    public String detail;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public LocalDateTime presentDate;

    public Integer operation; //操作（0：未完成评议；1：已完成评议;  2:操作过期）

    public String rejectReason;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public LocalDateTime completeTime;

    public Integer epoch;

    public Integer enable;

    public Double confidenceLevel;

    public Integer evaluatorId; //评估人id

    public Integer evaluatedId; //被评估人id

    public String evaluatorName; // 评估人姓名

    public String evaluatedName; //被评估人姓名
}
