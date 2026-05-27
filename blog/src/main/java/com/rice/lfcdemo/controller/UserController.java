package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("userInfo")
    public ResponseResult userInfo(){
        return userService.userInfo();
    }

    @PostMapping("/userInfo")
    public ResponseResult userInfor(@RequestBody User user){
        return userService.updateUserInfo(user);
    }

    @PostMapping("/registerUser")
    public ResponseResult registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

}
