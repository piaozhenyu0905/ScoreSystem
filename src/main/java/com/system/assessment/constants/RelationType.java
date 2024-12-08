package com.system.assessment.constants;

public enum RelationType {
    self(0, "自主评估"),
    fixed(1, "固定评估");

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    RelationType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (RelationType relationType : RelationType.values()) {
            if (relationType.getCode() == code) {
                return relationType.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (RelationType relationType : RelationType.values()) {
            if (relationType.description.equals(description)) {
                return relationType.code;
            }
        }
        return -1; // 返回 -1 代表没有找到对应的角色
    }
}
