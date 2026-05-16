package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
