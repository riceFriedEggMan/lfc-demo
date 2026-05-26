package com.rice.lfcdemo.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rice.lfcdemo.domain.login.LoginUser;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>().eq(User::getUserName, username);

        User user = userMapper.selectOne(wrapper);

        if (Objects.isNull(user)){
            throw new UsernameNotFoundException(username);
        }

        // todo 用户权限信息
        ArrayList<String> list = new ArrayList<>();
        list.add("admin");
        return new LoginUser(user, list);

    }
}
