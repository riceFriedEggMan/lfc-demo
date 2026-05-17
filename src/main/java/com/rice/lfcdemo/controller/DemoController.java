package com.rice.lfcdemo.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lfc")
public class DemoController {

    @Autowired
    private UserService userService;

    @GetMapping("/demoOne")
    public String demoOne(@RequestParam String name) {
        User user = new User();
        user.setUserName(name);
        user.setPassword("121312");
        return name;
    }

    @GetMapping("/getPassword")
    @PreAuthorize("hasAuthority('admin')")
    public String query(@RequestParam String name) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUserName, name);
        User user = userService.getOne(wrapper);
        return user.getPassword();
    }



}
