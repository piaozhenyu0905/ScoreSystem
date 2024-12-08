package com.system.assessment.constants;

public enum ScoringOperationType {
    NotConfirmed(0, "待确认"),
    Confirmed(1, "已确认"),
    Rejected(2, "已驳回");

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    ScoringOperationType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (ScoringOperationType scoringOperationType : ScoringOperationType.values()) {
            if (scoringOperationType.getCode() == code) {
                return scoringOperationType.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (ScoringOperationType scoringOperationType : ScoringOperationType.values()) {
            if (scoringOperationType.description.equals(description)) {
                return scoringOperationType.code;
            }
        }
        return -1; // 返回 -1 代表没有找到对应的角色
    }
}
