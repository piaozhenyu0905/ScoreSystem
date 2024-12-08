package com.system.assessment.exception;

import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author WKY
 */
public class CustomException extends RuntimeException {
    /**
     * 异常错误编码
     */
    @Getter
    private int code;
    /**
     * 异常信息
     */
    @Getter
    private String message;

    public CustomException(CustomExceptionType exceptionType, String message) {
        this.code = exceptionType.getCode();
        this.message = message;
    }


    public CustomException(CustomExceptionType exceptionType, ErrMsg errMsg) {
        this.code = exceptionType.getCode();
        this.message = errMsg.getMsg();
    }
}
