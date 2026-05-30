package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.entity.SysRoleMenu;


/**
 * 角色和菜单关联表(SysRoleMenu)表服务接口
 *
 * @author makejava
 * @since 2026-05-30 10:32:31
 */
public interface SysRoleMenuService extends IService<SysRoleMenu> {

    void deletRoleMenuByRoleId(Long id);
}
