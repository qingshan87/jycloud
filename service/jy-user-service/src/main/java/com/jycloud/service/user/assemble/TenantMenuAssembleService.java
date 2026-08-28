package com.jycloud.service.user.assemble;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycloud.api.user.constant.AdminRoleNameEnum;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.entity.Role;
import com.jycloud.service.user.cache.TenantMenuCache;
import com.jycloud.service.user.service.MenuService;
import com.jycloud.service.user.service.RoleMenuService;
import com.jycloud.service.user.service.RoleService;
import com.jycloud.tool.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 租户菜单聚合服务
 * <p>
 * 负责跨 Role / RoleMenu / Menu 组装租户全量菜单数据。
 * 租户全量菜单 = 租户管理员角色（tenant_admin）绑定的菜单。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantMenuAssembleService {

    private final RoleService roleService;
    private final RoleMenuService roleMenuService;
    private final MenuService menuService;
    private final TenantMenuCache tenantMenuCache;

    /**
     * 获取租户全部菜单（含按钮）
     *
     * @param tenantId 租户 ID
     * @return 菜单列表
     */
    public List<Menu> listTenantMenus(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return Collections.emptyList();
        }
        return CommonUtil.selectCacheByTemplate(
                () -> tenantMenuCache.getTenantMenusByTenantId(tenantId),
                () -> this.listTenantMenusFromDb(tenantId));
    }

    /**
     * 从数据库组装租户全量菜单并回写缓存
     */
    private List<Menu> listTenantMenusFromDb(String tenantId) {
        List<String> roleNames = new ArrayList<>(Arrays.asList(AdminRoleNameEnum.TENANT_ADMIN.getCode(), AdminRoleNameEnum.SUPER_ADMIN.getCode()));
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Role::getTenantId, tenantId)
                .eq(Role::getReadonly, 1)
                .in(Role::getRoleName, roleNames)
                .eq(Role::getStatus, 1);
        List<Role> roles = this.roleService.list(queryWrapper);
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> menuIds = new HashSet<>();
        for (Role role : roles) {
            Set<Long> roleMenuIds = roleMenuService.listMenuIdsByRoleId(role.getRoleId());
            menuIds.addAll(roleMenuIds);
        }
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }

        List<Menu> menus = menuService.list(new LambdaQueryWrapper<Menu>()
                .in(Menu::getMenuId, menuIds)
                .orderByAsc(Menu::getSort));
        if (menus == null) {
            menus = Collections.emptyList();
        }
        tenantMenuCache.cacheTenantMenus(menus, tenantId);
        return menus;
    }

}
