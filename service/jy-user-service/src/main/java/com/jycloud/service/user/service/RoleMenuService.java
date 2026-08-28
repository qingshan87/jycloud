package com.jycloud.service.user.service;

import com.jycloud.api.user.entity.RoleMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

/**
 * <p>
 * 角色菜单表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface RoleMenuService extends IService<RoleMenu> {

    /**
     * 设置角色菜单
     * @param roleId 角色ID
     * @param menuIds 菜单ID集合
     */
    void setRoleMenus(Long roleId, Set<Long> menuIds);

    /**
     * 查询角色绑定的菜单ID集合
     *
     * @param roleId 角色ID
     * @return 菜单ID集合
     */
    Set<Long> listMenuIdsByRoleId(Long roleId);

    /**
     * 查询绑定了指定菜单的角色所属的租户 ID 集合
     *
     * @param menuId 菜单 ID
     * @return 租户 ID 集合
     */
    Set<String> listTenantIdsByMenuId(Long menuId);
}
