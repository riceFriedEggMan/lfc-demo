package com.rice.lfcdemo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rice.lfcdemo.entity.Menu;

import java.util.List;


/**
 * 菜单权限表(Menu)表数据库访问层
 *
 * @author makejava
 * @since 2026-05-30 10:32:18
 */
public interface MenuMapper extends BaseMapper<Menu> {


    List<Long> selectMenuListByRoleId(Long roleId);

    List<String> selectPermsByUserId(long userId);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    List<Menu> selectAllRouterMenu();

}
