package com.system.assessment.constants;

public enum TaskType {
    SurroundingEvaluation(1, "周边评议");//普通用户

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    TaskType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.getCode() == code) {
                return taskType.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (TaskType taskType : TaskType.values()) {
            if (taskType.description.equals(description)) {
                return taskType.code;
            }
        }
        return -1; // 返回 -1 代表没有找到对应的角色
    }
}
