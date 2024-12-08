package com.system.assessment.constants;

public enum ProcessType {
    BuildRelationships(1, "建立评估矩阵关系"),
    Evaluation(2, "上级评估/周边评估"),
    ResultConsultation(3, "结果沟通"),
    ResultPublic(4, "结果发布");

    private final Integer code;
    private final String description;

    // 枚举的构造方法默认为私有的
    ProcessType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }
    public int getCode() { return code; }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(int code) {
        for (ProcessType processType : ProcessType.values()) {
            if (processType.getCode() == code) {
                return processType.getDescription();
            }
        }
        return null;
    }

    public static int getCodeByDescription(String description) {
        for (ProcessType processType : ProcessType.values()) {
            if (processType.description.equals(description)) {
                return processType.code;
            }
        }
        return -1; // 返回 -1 代表没有找到对应的角色
    }
}
