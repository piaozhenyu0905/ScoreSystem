package com.system.assessment.exception;

import lombok.Getter;

/**
 * 异常分类的枚举，把异常分类固化下来、
 */
public enum CustomExceptionType {
    /**
     * 客户端错误，如输入校验失败
     */
    USER_INPUT_ERROR(400,"Bad Request/用户异常"),
    /**
     * 服务器端错误，如各种编码bug或服务内部异常
     */
    SYSTEM_ERROR (500,"Internal Server Error/系统异常"),
    /**
     * 未知错误，即没有被发现并转换为CustomException的异常
     */
    OTHER_ERROR(999,"Unknown Error/未知异常");

    CustomExceptionType(int code, String typeDesc) {
        this.code = code;
        this.typeDesc = typeDesc;
    }

    /**
     * 异常类型描述
     */
    @Getter
    private String typeDesc;

    /**
     * 异常编码
     */
    @Getter
    private int code;
}
