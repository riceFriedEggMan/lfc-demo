package com.rice.lfcdemo.service.impl;

import com.rice.lfcdemo.domain.login.LoginUser;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.service.LoginService;
import com.rice.lfcdemo.utils.JwtUtils;
import com.rice.lfcdemo.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public ResponseResult login(User user) {

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());

        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();

        String userId = loginUser.getUser().getUserId();

        // todo 获取jwt，用户信息保存至redis
        String jwt = JwtUtils.createJWT(userId);
        redisCache.setCacheObject("login:" + userId, loginUser);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("token", jwt);
        return new ResponseResult<>(200, "登录成功", map);
    }

    @Override
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getUserId();
        redisCache.deleteObject("login:" + userId);
        return ResponseResult.ok();
    }
}
