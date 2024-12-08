package com.system.assessment.constants;

public enum OperationType {
    NOTFINISHED(0, "未完成"),
    FINISHED(1, "已完成"),
    INVALUED(2,"操作已无效");

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    OperationType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.getCode() == code) {
                return operationType.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (OperationType operationType : OperationType.values()) {
            if (operationType.description.equals(description)) {
                return operationType.code;
            }
        }
        return -1;
    }
}
