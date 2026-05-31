package com.rice.lfcdemo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.rice.lfcdemo.entity.Menu;
import com.rice.lfcdemo.entity.vo.MenuTreeVo;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2026-05-30 10:32:18
 */
public interface MenuService extends IService<Menu> {

    List<Menu> selectMenuList(Menu menu);

    boolean hasChild(Long menuId);

    List<MenuTreeVo> buildMenuTreeList(List<Menu> menus);

    List<Long> selectMenuListByRoleId(Long roleId);

    List<String> selectPermsByUserId(long userId);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);
}
