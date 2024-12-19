package com.system.assessment.exception;


import io.netty.channel.ConnectTimeoutException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;


/**
 * 全局异常处理器
 * ControllerAdvice注解监听所有的Controller，一旦抛出CustomException，
 * 就会在@ExceptionHandler(CustomException.class)对该异常进行处理。
 *
 * 例举一些常见的异常进行特殊化处理
 *
 * @author WKY
 */
@Slf4j
@ControllerAdvice
public class WebExceptionController {

    @Resource
    private HttpServletRequest request;

    //访问拒绝抛出给AccessDeniedHandlerImpl处理
    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public void accessDeniedException(AccessDeniedException e) throws AccessDeniedException {
        log.error("全局异常处理器将AccessDeniedException异常抛出");
        throw e;
    }


    //自定义异常
    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public ResponseResult<String> handleAllExceptions(CustomException ex) {
        String message = ex.getMessage();
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR,  message);
        log.error("全局异常处理器将handleAllExceptions异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }

    //    输入违反了sql约束，如不可为空
    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public ResponseResult sqlException(SQLException e){
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "请输入正确格式的输入"+e.getMessage());
//        AppErrorLog(customException);
        log.error("全局异常处理器将sqlException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }



    // 请求参数检验不通过（如@NotNull却page=）
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult handleBindException(ConstraintViolationException ex) {
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "参数校验不通过");
//        AppErrorLog(customException);
        log.error("全局异常处理器将handleBindException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }


    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseResult handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "缺少请求参数");
//        AppErrorLog(customException);
        log.error("全局异常处理器将handleMissingServletRequestParameterException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }


    // 不支持的HTTP方法异常
    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseResult handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        // 创建并返回您自定义的错误响应
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "接收到一个不支持的媒体类型（MIME type）:" + e.getContentType());
        ResponseResult errorResponse = ResponseResult.error(customException);
        log.error("全局异常处理器将handleHttpMediaTypeNotSupportedException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }


    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    //处理@Valid发出的异常
    public ResponseResult throwCustomException(MethodArgumentNotValidException methodArgumentNotValidException){

        FieldError fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
        String field = fieldError.getField();
        String code = fieldError.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "请输入正确格式的输入："+ message);
//        AppErrorLog(customException);
        log.error("全局异常处理器将throwCustomException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }

    @ResponseBody
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseResult UsernameNotFoundException(UsernameNotFoundException e){
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户名或密码错误");
        log.error("用户名不存在");
        return ResponseResult.error(customException);
    }

    @ResponseBody
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseResult BadCredentialsException(BadCredentialsException e){
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "用户名或密码错误");
        log.error("密码错误");
        return ResponseResult.error(customException);
    }


    @ResponseBody
    @ExceptionHandler(DataAccessException.class)
    public ResponseResult handleUncategorizedSQLException(DataAccessException ex) {

        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "数据库操作错误");
        log.error("数据库错误发生: ",ex);
        log.error("全局异常处理器将handleUncategorizedSQLException异常抛出,信息为"+customException.getMessage());
        return ResponseResult.error(customException);
    }

    //表的字段类型与input的属性类型不一致
    @ResponseBody
    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseResult sqlSyntaxErrorException(SQLSyntaxErrorException e){
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "数据库表的字段类型与input的属性类型不一致");
//        AppErrorLog(customException);
        log.error("全局异常处理器将sqlSyntaxErrorException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }


    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ResponseResult handleBindException(BindException ex) {

        FieldError fieldError = ex.getBindingResult().getFieldError();
        String field = fieldError.getField();
        String code = fieldError.getDefaultMessage();
        String message = String.format("%s:%s", field, code);
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "参数绑定失败："+ message);
//        AppErrorLog(customException);
        log.error("全局异常处理器将handleBindException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }

    //客户端和服务器进行数据交互超时
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult hanldeHttpMessageNotReadableException(HttpMessageNotReadableException  e){
        log.error("请求参数的类型不符");
        CustomException customException =
                new CustomException(CustomExceptionType.SYSTEM_ERROR, "请求参数的类型不符");
//        AppErrorLog(customException);
        log.error("全局异常处理器将hanldeHttpMessageNotReadableException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }


    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.info("不支持当前请求方法");
        CustomException customException =
                new CustomException(CustomExceptionType.USER_INPUT_ERROR, "不支持当前请求方法");
//        AppErrorLog(customException);
        log.error("全局异常处理器将handleHttpRequestMethodNotSupportedException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }

    //建立连接的超时
    @ResponseBody
    @ExceptionHandler(ConnectTimeoutException.class)
    public ResponseResult connectTimeoutException(ConnectTimeoutException e){
        log.error("建立连接超时");
        CustomException customException =
                new CustomException(CustomExceptionType.SYSTEM_ERROR, "建立连接超时");
//        AppErrorLog(customException);
        log.error("全局异常处理器将connectTimeoutException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }

    //客户端和服务器进行数据交互超时
    @ResponseBody
    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseResult socketTimeoutException(SocketTimeoutException e){
        log.error("客户端和服务器进行数据交互超时");
        CustomException customException =
                new CustomException(CustomExceptionType.SYSTEM_ERROR, "客户端和服务器进行数据交互超时");
//        AppErrorLog(customException);
        log.error("全局异常处理器将socketTimeoutException异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }



    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseResult exception(Exception e) {

        // 将999异常信息持久化处理，方便运维人员处理
        log.error("\n999 Unknown Error!\n" +
                request2Str(request) + "\n" +
                exception2Str(e) + "\n"+
                "---------------------------------------------------------------------------------------------------------\n");

        CustomException customException =
                new CustomException(CustomExceptionType.OTHER_ERROR, "未知异常");
//        AppErrorLog(customException);
        log.error("全局异常处理器将exception异常抛出,信息为"+customException.toString());
        return ResponseResult.error(customException);
    }

    @SneakyThrows
    private String request2Str(HttpServletRequest q) {
        if (q == null) {
            return "";
        }
        String str;
        StringBuilder wholeStr = new StringBuilder();
        wholeStr.append("请求方法：").append(request.getMethod()).append("\n")
                .append("请求URL:").append(q.getRequestURL());
//                .append("请求实体：");

//        BufferedReader br = q.getReader();
//        while ((str = br.readLine()) != null) {
//            wholeStr.append(str);
//        }

        return wholeStr.toString();
    }

    private String exception2Str(Exception e) {
        if (e == null) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return "异常信息：" + stringWriter.toString();
    }



}
