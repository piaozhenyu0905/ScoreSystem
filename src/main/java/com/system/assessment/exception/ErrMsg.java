package com.system.assessment.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 错误消息枚举类
 *
 * @author WKY
 */

public enum ErrMsg {


    NO_UNIVERSAL_TEMPLATE_EXISTS(1000,"不存在通用模板"),
    ANSWER_PARAMETERS_CANNOT_BE_NULL(1000,"问卷模板id或者患者id或者随访情况为null"),

    ANSWER_CONTENT_CANNOT_BE_NULL(1000,"回答内容不能为空"),
    MODIFICATION_FAILURE(1000,"修改失败"),
    SOME_ANSWERS_ARE_NOT_LEGAL(1000,"部分答案不合法");

    @Getter
    private final int code;
    private final String msg;

    @JsonValue
    public String getMsg() {
        return msg;
    }

    ErrMsg(int code, String msg) {
        this.msg = msg;
        this.code = code;
    }
}
