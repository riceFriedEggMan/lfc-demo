package com.rice.lfcdemo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rice.lfcdemo.entity.Menu;
import com.rice.lfcdemo.entity.vo.MenuTreeVo;
import com.rice.lfcdemo.mapper.MenuMapper;
import com.rice.lfcdemo.service.MenuService;
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
