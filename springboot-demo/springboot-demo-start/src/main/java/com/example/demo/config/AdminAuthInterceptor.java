package com.example.demo.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 管理员鉴权拦截器
 *
 * 拦截 /api/admin/** 路径,要求请求头 X-Admin-Password 携带 MD5 与 PASSWORD_MD5 一致。
 *
 * 与现有 PlayerServiceImpl#PASSWORD_MD5 保持同一密码,前端从 sessionStorage('playerManageAuth') 取
 * 真实 MD5(由 PasswordModal 设置)写入请求头。MD5 不匹配返回 403。
 */
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String PASSWORD_MD5 = "6b09e658e9143361008d26983cc738ec";

    private static final String HEADER = "X-Admin-Password";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检放行,避免 CORS 失败
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String password = request.getHeader(HEADER);
        if (PASSWORD_MD5.equals(password)) {
            return true;
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":\"3010\",\"message\":\"管理员鉴权失败\",\"data\":null}");
        return false;
    }
}
