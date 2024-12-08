package com.system.assessment.constants;

public enum Role {
    superAdmin(3, "superAdmin"), //超级管理员
    SecondAdmin(2, "secondAdmin"), //二级管理员
    firstAdmin(1, "firstAdmin"), //一级管理员
    normal(0, "normal");//普通用户

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    Role(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (Role role : Role.values()) {
            if (role.getCode() == code) {
                return role.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (Role role : Role.values()) {
            if (role.description.equals(description)) {
                return role.code;
            }
        }
        return -1; // 返回 -1 代表没有找到对应的角色
    }
}
