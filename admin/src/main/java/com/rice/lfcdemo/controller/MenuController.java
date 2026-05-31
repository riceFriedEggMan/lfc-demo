package com.rice.lfcdemo.controller;

import com.rice.lfcdemo.domain.ResponseResult;
import com.rice.lfcdemo.entity.Menu;
import com.rice.lfcdemo.entity.vo.MenuTreeVo;
import com.rice.lfcdemo.entity.vo.MenuVo;
import com.rice.lfcdemo.entity.vo.RoleMenuTreeSelectVo;
import com.rice.lfcdemo.service.MenuService;
import com.rice.lfcdemo.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult list(Menu menu) {
        List<Menu> menuList = menuService.selectMenuList(menu);
        List<MenuVo> menuVos = BeanCopyUtils.copyList(menuList, MenuVo.class);
        return ResponseResult.ok(menuVos);
    }

    @PostMapping("/add")
    public ResponseResult add(@RequestBody Menu menu) {
        menuService.save(menu);
        return ResponseResult.ok();
    }

    @GetMapping("/{menuId}")
    public ResponseResult getInfo(@PathVariable Long menuId) {
        return ResponseResult.ok(menuService.getById(menuId));
    }

    @PutMapping("/edit")
    public ResponseResult edit(@RequestBody Menu menu) {
        if (menu.getId().equals(menu.getParentId())){
            return ResponseResult.errorResult(500,"修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menuService.updateById(menu);
        return ResponseResult.ok();
    }

    @DeleteMapping("/delete")
    public ResponseResult delete(@PathVariable Long menuId) {
        if (menuService.hasChild(menuId)){
            return ResponseResult.errorResult(500,"存在子菜单不允许删除");
        }
        menuService.removeById(menuId);
        return ResponseResult.ok();
    }

    @GetMapping("/treeSelect")
    public ResponseResult treeSelect(){
        List<Menu> menus = menuService.selectMenuList(new Menu());
        List<MenuTreeVo> menuTreeVos = menuService.buildMenuTreeList(menus);
        return ResponseResult.ok(menuTreeVos);
    }

    @GetMapping("/roleMenuTreeSelect/{roleId}")
    public ResponseResult roleMenuTreeSelect(@PathVariable Long roleId){
        List<Menu> menus = menuService.selectMenuList(new Menu());
        List<Long> checkedKeys = menuService.selectMenuListByRoleId(roleId);
        List<MenuTreeVo> menuTreeVos = menuService.buildMenuTreeList(menus);
        RoleMenuTreeSelectVo roleMenuTreeSelectVo = new RoleMenuTreeSelectVo(checkedKeys, menuTreeVos);
        return ResponseResult.ok(roleMenuTreeSelectVo);
    }
}
