package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.domain.enums.AppHttpCodeEnum;
import com.rice.lfcdemo.domain.login.LoginUser;
import com.rice.lfcdemo.entity.Menu;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.entity.vo.AdminUserInfoVo;
import com.rice.lfcdemo.entity.vo.RoutersVo;
import com.rice.lfcdemo.entity.vo.UserInfoVo;
import com.rice.lfcdemo.exception.SystemException;
import com.rice.lfcdemo.service.LoginService;
import com.rice.lfcdemo.service.MenuService;
import com.rice.lfcdemo.service.RoleService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import com.rice.lfcdemo.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/admin/login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user) {
        if (!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }

    @GetMapping("getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        //获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        //根据用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getUserId());
        //根据用户id查询角色信息
        List<String> roleKeyList = roleService.selectRoleKeyByUserId(loginUser.getUser().getUserId());

        //获取用户信息
        User user = loginUser.getUser();
        UserInfoVo userInfoVo = BeanCopyUtils.copy(user, UserInfoVo.class);
        //封装数据返回

        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(perms,roleKeyList,userInfoVo);
        return ResponseResult.ok(adminUserInfoVo);
    }

    @GetMapping("getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        Long userId = SecurityUtils.getUserId();
        //查询menu 结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        //封装数据返回
        return ResponseResult.ok(new RoutersVo(menus));
    }
}
