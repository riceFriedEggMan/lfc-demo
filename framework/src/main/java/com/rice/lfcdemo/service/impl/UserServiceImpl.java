package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.domain.enums.AppHttpCodeEnum;
import com.rice.lfcdemo.entity.SysUserRole;
import com.rice.lfcdemo.entity.User;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.entity.vo.UserInfoVo;
import com.rice.lfcdemo.entity.vo.UserVo;
import com.rice.lfcdemo.exception.SystemException;
import com.rice.lfcdemo.mapper.UserMapper;
import com.rice.lfcdemo.service.SysUserRoleService;
import com.rice.lfcdemo.service.UserService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import com.rice.lfcdemo.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2026-05-09 09:07:31
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Override
    public ResponseResult userInfo() {
        Long userId = SecurityUtils.getUserId();
        User user = getById(userId);
        UserInfoVo userInfoVo = BeanCopyUtils.copy(user, UserInfoVo.class);
        return ResponseResult.ok(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        this.updateById(user);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult registerUser(User user) {
        if (!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if (!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        if (userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        save(user);
        return ResponseResult.ok();
    }

    @Override
    public ResponseResult pageList(User user, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(user.getUserName()),User::getUserName,user.getUserName());
        queryWrapper.eq(!Objects.isNull(user.getStatus()),User::getStatus,user.getStatus());
        queryWrapper.eq(StringUtils.hasText(user.getPhone()),User::getPhone,user.getPhone());

        Page<User> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,queryWrapper);

        List<User> users = page.getRecords();
        List<UserVo> userVos = BeanCopyUtils.copyList(users, UserVo.class);

        PageVo pageVo = new PageVo(userVos, page.getTotal());
        return ResponseResult.ok(pageVo);
    }

    @Override
    public boolean checkUserNameUnique(String userName) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getUserName,userName)) == 0;
    }

    @Override
    public boolean checkPhoneUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getPhone,user.getPhone())) == 0;
    }

    @Override
    public boolean checkEmailUnique(User user) {
        return count(Wrappers.<User>lambdaQuery().eq(User::getEmail,user.getEmail())) == 0;
    }

    @Override
    public ResponseResult addUser(User user) {
        String encode = passwordEncoder.encode(user.getPassword());
        user.setPassword(encode);
        save(user);

        // todo 增加角色
        Long[] roleIds = user.getRoleIds();
        if (roleIds != null && roleIds.length > 0) {
            insertRoles(user);
        }
        return ResponseResult.ok();

    }

    @Override
    public ResponseResult edit(User user) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,user.getUserId());
        sysUserRoleService.remove(wrapper);
        insertRoles(user);

        updateById(user);
        return ResponseResult.ok();
    }

    private void insertRoles(User user) {
        List<SysUserRole> userRoles = Arrays
                .stream(user.getRoleIds())
                .map(roleId -> new SysUserRole(user.getUserId(), roleId))
                .collect(Collectors.toList());
        sysUserRoleService.saveBatch(userRoles);
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return count(queryWrapper) > 0;
    }
}
