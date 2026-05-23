package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.mapper.UserMapper;
import com.rice.lfcdemo.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2026-05-09 09:07:31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    public void test(String id){
        User byId = getById(id);
    }
}
