package com.system.assessment.config.handler;

import com.alibaba.fastjson.JSON;
import com.system.assessment.exception.ResponseResult;
import com.system.assessment.utils.FastJSONUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        ResponseResult result = new ResponseResult(HttpStatus.FORBIDDEN.value(), "用户无权进行访问");
        String json = JSON.toJSONString(result);
        FastJSONUtil.error(response,json);
    }
}
