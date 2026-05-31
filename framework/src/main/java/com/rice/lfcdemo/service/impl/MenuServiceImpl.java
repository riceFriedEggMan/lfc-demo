package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.constants.SystemConstants;
import com.rice.lfcdemo.entity.Menu;
import com.rice.lfcdemo.entity.vo.MenuTreeVo;
import com.rice.lfcdemo.mapper.MenuMapper;
import com.rice.lfcdemo.service.MenuService;
import com.rice.lfcdemo.utils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2026-05-30 10:32:18
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> selectMenuList(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        //menuName模糊查询
        queryWrapper.like(StringUtils.hasText(menu.getMenuName()),Menu::getMenuName,menu.getMenuName());
        queryWrapper.eq(StringUtils.hasText(menu.getStatus()),Menu::getStatus,menu.getStatus());
        //排序 parent_id和order_num
        queryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
        List<Menu> menus = list(queryWrapper);;
        return menus;
    }

    @Override
    public boolean hasChild(Long menuId) {
        return count(Wrappers.<Menu>lambdaQuery().eq(Menu::getParentId, menuId)) > 0;
    }

    @Override
    public List<MenuTreeVo> buildMenuTreeList(List<Menu> menus) {
        List<MenuTreeVo> menuTreeVos = menus
                .stream()
                .map(m -> new MenuTreeVo(m.getId(), m.getMenuName(), m.getParentId(), null))
                .collect(Collectors.toList());
        menuTreeVos
                .stream()
                .filter(m -> m.getParentId().equals(0L))
                .map(m -> {
                    m.setChildren(getChild(menuTreeVos, m));
                    return m;
                }).collect(Collectors.toList());
        return menuTreeVos;
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        return getBaseMapper().selectMenuListByRoleId(roleId);
    }

    @Override
    public List<String> selectPermsByUserId(long userId) {
        if (userId == 1L){
            LambdaQueryWrapper<Menu> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.in(Menu::getMenuType, SystemConstants.MENU, SystemConstants.BUTTON);
            queryWrapper.eq(Menu::getStatus, SystemConstants.NORMAL);
            List<Menu> list = list(queryWrapper);
            List<String> perms = list.stream().map(Menu::getPerms).collect(Collectors.toList());
            return perms;
        }
        return getBaseMapper().selectPermsByUserId(userId);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin()){
            //如果是 获取所有符合要求的Menu
            menus = menuMapper.selectAllRouterMenu();
        }else{
            //否则  获取当前用户所具有的Menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }

        //构建tree
        //先找出第一层的菜单  然后去找他们的子菜单设置到children属性中
        List<Menu> menuTree = builderMenuTree(menus,0L);
        return menuTree;

    }

    private List<Menu> builderMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {menu.setChildren(getChildren(menu, menus)); return menu;})
                .collect(Collectors.toList());
        return menuTree;
    }

    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m-> {m.setChildren(getChildren(m,menus)); return m;})
                .collect(Collectors.toList());
        return childrenList;
    }

    private List<MenuTreeVo> getChild(List<MenuTreeVo> menuTreeVos, MenuTreeVo menuTreeVo) {
        return menuTreeVos
                .stream()
                .filter(m -> Objects.equals(m.getParentId(), menuTreeVo.getId()))
                .map(m -> {
                    m.setChildren(getChild(menuTreeVos, m));
                    return m;
                })
                .collect(Collectors.toList());
    }
}
