package com.system.assessment.constants;

public enum BusinessType {
    RAD(0, "研发"),
    NONRAD(1, "非研发");

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    BusinessType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (BusinessType businessType : BusinessType.values()) {
            if (businessType.getCode() == code) {
                return businessType.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (BusinessType businessType : BusinessType.values()) {
            if (businessType.description.equals(description)) {
                return businessType.code;
            }
        }
        return -1;
    }
}
