package com.rice.lfcdemo.controller;


import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
        userService.save(user);
        return name;
    }
}
