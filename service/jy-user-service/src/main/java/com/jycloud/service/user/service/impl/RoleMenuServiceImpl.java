package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycloud.api.user.entity.RoleMenu;
import com.jycloud.service.user.repository.mapper.RoleMenuMapper;
import com.jycloud.service.user.service.RoleMenuService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.exception.SysException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色菜单表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setRoleMenus(Long roleId, Set<Long> menuIds) {
        if (roleId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        // 删除原有角色菜单关联
        this.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));

        // 保存新的角色菜单关联
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<RoleMenu> roleMenus = menuIds.stream()
                .filter(menuId -> menuId != null)
                .map(menuId -> {
                    RoleMenu roleMenu = new RoleMenu();
                    roleMenu.setRoleId(roleId);
                    roleMenu.setMenuId(menuId);
                    return roleMenu;
                })
                .collect(Collectors.toList());
        if (!roleMenus.isEmpty()) {
            this.saveBatch(roleMenus);
        }
    }

    @Override
    public Set<Long> listMenuIdsByRoleId(Long roleId) {
        if (roleId == null) {
            return Collections.emptySet();
        }
        return this.list(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId))
                .stream()
                .map(RoleMenu::getMenuId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> listTenantIdsByMenuId(Long menuId) {
        if (menuId == null) {
            return Collections.emptySet();
        }
        Set<String> tenantIds = baseMapper.listTenantIdsByMenuId(menuId);
        return tenantIds != null ? tenantIds : Collections.emptySet();
    }
}
