package com.project.order_service.config;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static com.alibaba.fastjson.JSON.toJSONString;

@Data
public class CommonResult {
    private int code;
    private String message;

    public static CommonResult forbiddenFailure(String message, int code) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static class CustomAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {
            int statusCode = HttpServletResponse.SC_FORBIDDEN;  // 403
            response.setStatus(statusCode);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSONObject.toJSONString(forbiddenFailure("You are not authorized!", statusCode)));
        }

    }

    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            int statusCode = HttpServletResponse.SC_UNAUTHORIZED;  // 401
            response.setStatus(statusCode);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(toJSONString(forbiddenFailure("You are not authenticated!", statusCode)));
        }
    }
}
