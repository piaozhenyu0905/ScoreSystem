package com.system.assessment.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FastJSONUtil {

    public static void error(HttpServletResponse response, String msg) throws ServletException, IOException {
        // 创建一个全局的序列化配置
//        SerializeConfig serializeConfig = DateTimeUtil.dateFormat("yyyy-MM-dd HH:mm:ss");
//        ResponseResult error = ResponseResult.error(code,msg);
//        String notLogin = JSONObject.toJSONString(msg);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(msg);
    }
}
