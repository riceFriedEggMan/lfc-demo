package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.domain.enums.AppHttpCodeEnum;
import com.rice.lfcdemo.entity.Role;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.entity.vo.UserInfoAndRoleIdsVo;
import com.rice.lfcdemo.exception.SystemException;
import com.rice.lfcdemo.service.RoleService;
import com.rice.lfcdemo.service.UserService;
import com.rice.lfcdemo.utils.SecurityUtils;
import jakarta.servlet.http.PushBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/page")
    public ResponseResult list(User user, Integer pageNum, Integer pageSize) {
        return userService.pageList(user, pageNum, pageSize);
    }

    @PostMapping("/add")
    public ResponseResult add(@RequestBody User user) {
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!userService.checkUserNameUnique(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if (!userService.checkPhoneUnique(user)){
            throw new SystemException(AppHttpCodeEnum.PHONENUMBER_EXIST);
        }
        if (!userService.checkEmailUnique(user)){
            throw new SystemException(AppHttpCodeEnum.EMAIL_EXIST);
        }
        return userService.addUser(user);
    }

    @DeleteMapping("/{ids}")
    public ResponseResult delete(@PathVariable List<Long> ids) {
        if (ids.contains(SecurityUtils.getUserId())){
            return ResponseResult.errorResult(500, "不能删除当前登录用户");
        }
        userService.removeByIds(ids);
        return ResponseResult.ok();
    }


    @GetMapping("/{userId}")
    public ResponseResult getUserInfoAndRoleIds(@PathVariable(value = "userId") Long userId) {
        List<Role> roles = roleService.selectRoleAll();
        User user = userService.getById(userId);
        List<Long> roleIds = roleService.selectRoleIdByUserId(userId);
        UserInfoAndRoleIdsVo userInfoAndRoleIdsVo = new UserInfoAndRoleIdsVo(user, roles, roleIds);
        return ResponseResult.ok(userInfoAndRoleIdsVo);
    }

    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody User user) {
        return userService.edit(user);
    }


}
