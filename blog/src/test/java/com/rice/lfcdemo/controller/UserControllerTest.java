package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.service.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Resource
    private UserService userService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Test
    void userInfo() {
        System.out.println(passwordEncoder.encode("1234"));
    }
}