package com.rice.lfcdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.lfcdemo.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2026-05-30 08:57:56
 */
public interface RoleMapper extends BaseMapper<Role> {
    List<Long> selectRoleIdByUserId(Long id);
}
