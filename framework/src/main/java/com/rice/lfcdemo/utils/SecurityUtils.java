package com.rice.lfcdemo.utils;

import com.rice.lfcdemo.domain.login.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private static boolean admin;

    public static LoginUser getLoginUser() {
        return (LoginUser) getAuthentication().getPrincipal();
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    public static Long getUserId() {
        return getLoginUser().getUser().getUserId();
    }

    public static boolean isAdmin() {
        Long id = getLoginUser().getUser().getUserId();
        return id != null && 1L == id;
    }
}
