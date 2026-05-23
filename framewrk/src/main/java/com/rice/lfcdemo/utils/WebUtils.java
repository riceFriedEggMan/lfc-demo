package com.rice.lfcdemo.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class WebUtils {
    public static String renderString(HttpServletResponse response, String json) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().print(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
