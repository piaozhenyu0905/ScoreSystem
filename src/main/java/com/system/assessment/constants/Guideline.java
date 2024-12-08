package com.system.assessment.constants;

public enum Guideline {
    Ideology(1, "思想上: 开放进取(O)"),
    Style(2, "作风上: 把打胜仗作为信仰(W)"),
    Business(3, "业务上: 创新求实(I)"),
    Work(4, "工作中: 信任互助(H)"),
    Employee(5, "员工间: 尊重平等(R)"),
    Behavior(6, "行为上: 业精于勤(D)"),
    Execution(7, "执行力: 使命必达(M)"),
    Team(8, "团队上: 五湖四海(F)"),
    Growth(9, "成长上: 坚持自省(I)");

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    Guideline(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (Guideline guideline : Guideline.values()) {
            if (guideline.getCode() == code) {
                return guideline.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (Guideline guideline : Guideline.values()) {
            if (guideline.description.equals(description)) {
                return guideline.code;
            }
        }
        return -1; // 返回 -1 代表没有找到对应的角色
    }
}
