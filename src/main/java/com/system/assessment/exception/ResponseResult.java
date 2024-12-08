package com.system.assessment.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//当您使用此注解时，它可以确保只有非空的字段才被序列化到 JSON 中。
// 换句话说，如果对象的某个属性值为 null，那么这个属性就不会出现在最终的 JSON 输出中。
//@JsonInclude(JsonInclude.Include.NON_NULL)

@Data
@NoArgsConstructor
public class ResponseResult<T> {
    /**
     * 状态码
     */

    private Integer code;
    /**
     * 提示信息，如果有错误时，前端可以获取该字段进行提示
     */

    private String msg;
    /**
     * 查询到的结果数据，
     */

    private Date timeStamp;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getTimeStamp() {
        return timeStamp;
    }

    private T data;

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    //增删改 成功响应
    public static ResponseResult success(){
        ResponseResult responseResult = new ResponseResult(200,"请求成功",null);
        responseResult.setTimeStamp(new Date());
        return responseResult;
    }

    //增删改 成功响应
    public static ResponseResult success(String msg){
        ResponseResult responseResult = new ResponseResult(200,msg,null);
        responseResult.setTimeStamp(new Date());
        return responseResult;
    }

    //增删改 成功响应
    public static ResponseResult success(String msg, Object data){
        ResponseResult responseResult = new ResponseResult(200,msg,data);
        responseResult.setTimeStamp(new Date());
        return responseResult;
    }

    //查询 成功响应
    public static ResponseResult success(Object data){
        ResponseResult responseResult = new ResponseResult(200,"请求成功",data);
        responseResult.setTimeStamp(new Date());
        return responseResult;
    }

    //失败响应
    public static ResponseResult error(Integer code, String msg){

        ResponseResult responseResult = new ResponseResult();
        responseResult.setTimeStamp(new Date());
        responseResult.setCode(code);
        responseResult.setMsg(msg);
        return responseResult;
    }

    public static ResponseResult error(CustomException customException){

        ResponseResult responseResult = new ResponseResult();
        responseResult.setTimeStamp(new Date());
        responseResult.setCode(customException.getCode());
        responseResult.setMsg(customException.getMessage());
        return responseResult;
    }

}