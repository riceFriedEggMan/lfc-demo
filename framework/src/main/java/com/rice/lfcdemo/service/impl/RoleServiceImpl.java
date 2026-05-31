package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Role;
import com.rice.lfcdemo.entity.SysRoleMenu;
import com.rice.lfcdemo.entity.vo.PageVo;
import com.rice.lfcdemo.mapper.RoleMapper;
import com.rice.lfcdemo.service.RoleService;
import com.rice.lfcdemo.service.SysRoleMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2026-05-30 08:57:56
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    @Override
    public List<Role> selectRoleAll() {
        return list(Wrappers.<Role>lambdaQuery().eq(Role::getStatus, SystemConstants.NORMAL));
    }

    @Override
    public List<Long> selectRoleIdByUserId(Long userId) {
        return getBaseMapper().selectRoleIdByUserId(userId);
    }

    @Override
    public ResponseResult selectRolePage(Role role, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(role.getRoleName()),Role::getRoleName,role.getRoleName());
        lambdaQueryWrapper.eq(StringUtils.hasText(role.getStatus()),Role::getStatus,role.getStatus());
        lambdaQueryWrapper.orderByAsc(Role::getRoleSort);

        Page<Role> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,lambdaQueryWrapper);

        //转换成VO
        List<Role> roles = page.getRecords();

        PageVo pageVo = new PageVo();
        pageVo.setTotal(page.getTotal());
        pageVo.setRows(roles);
        return ResponseResult.ok(pageVo);
    }

    @Override
    public void insertRole(Role role) {
        save(role);
        if (role.getMenuIds() != null && role.getMenuIds().length > 0) {
            insertMenu(role);
        }
    }

    @Override
    public void updateRole(Role role) {
        updateById(role);
        sysRoleMenuService.deletRoleMenuByRoleId(role.getId());
        insertMenu(role);
    }

    @Override
    public List<String> selectRoleKeyByUserId(long userId) {
        if(userId == 1L){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        return getBaseMapper().selectPermsByUserId(userId);
    }

    private void insertMenu(Role role) {
        List<SysRoleMenu> sysRoleMenus = Arrays
                .stream(role.getMenuIds())
                .map(menuId -> new SysRoleMenu(role.getId(), menuId))
                .collect(Collectors.toList());
        sysRoleMenuService.saveBatch(sysRoleMenus);
    }
}
