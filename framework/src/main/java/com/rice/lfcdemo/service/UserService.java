package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.User;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2026-05-09 09:07:30
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult registerUser(User user);


}
