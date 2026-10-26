package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.vo.MenuVO;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface MenuService extends IService<Menu> {

    /**
     * 新增菜单/按钮
     *
     * @param menu 菜单/按钮实体
     * @return 保存后的菜单/按钮
     */
    Menu create(Menu menu);

    /**
     * 修改菜单/按钮（类型、所属应用不允许修改）
     *
     * @param menu 菜单/按钮实体
     * @return 更新后的菜单/按钮
     */
    Menu update(Menu menu);

    /**
     * 删除菜单/按钮（存在子节点时禁止删除）
     *
     * @param menuId 菜单/按钮ID
     */
    void deleteMenu(Long menuId);

    /**
     * 根据主键查询菜单/按钮
     *
     * @param menuId 菜单/按钮ID
     * @return 菜单/按钮
     */
    Menu getMenuById(Long menuId);

    /**
     * 查询指定应用的菜单树（含按钮）
     *
     * @param clientId 应用ID
     * @return 菜单树
     */
    List<MenuVO> listMenuTree(String clientId);
}
