package com.jycloud.service.user.assemble;

import com.alibaba.fastjson2.JSON;
import com.jycloud.api.user.constant.AuthorityCategoryEnum;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.service.user.cache.PermissionVersionCache;
import com.jycloud.service.user.cache.UserMenuCache;
import com.jycloud.service.user.service.RoleMenuService;
import com.jycloud.service.user.service.UserAuthorityService;
import com.jycloud.tool.common.util.CommonUtil;
import com.jycloud.tool.common.util.DtoEntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户菜单聚合服务
 * <p>
 * 负责跨 UserAuthority / RoleMenu 组装用户在指定租户下关联的 menuId 集合，
 * 用户菜单 = 用户直接绑定的菜单（category=1）+ 用户角色绑定的菜单。
 * 缓存仅存 menuId，菜单详情由租户菜单缓存（{@link TenantMenuAssembleService}）统一提供。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserMenuAssembleService {

    private static final int CATEGORY_MENU = 0;
    private static final long TOP_PARENT_ID = 0L;

    private final UserAuthorityService userAuthorityService;
    private final RoleMenuService roleMenuService;
    private final UserMenuCache userMenuCache;
    private final UserRoleAssembleService userRoleAssembleService;
    private final PermissionVersionCache permissionVersionCache;
    private final TenantMenuAssembleService tenantMenuAssembleService;

    /**
     * 获取用户在指定租户下关联的 menuId 集合（含按钮）
     * <p>
     * 缓存读取时与租户权限版本号比对，版本不一致则回源重算并回写缓存。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 菜单 ID 集合
     */
    public List<Long> listUserMenuIds(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return Collections.emptyList();
        }
        long version = permissionVersionCache.getVersion(tenantId);
        return CommonUtil.selectCacheByTemplate(
                () -> userMenuCache.getUserMenuIdsByUserId(String.valueOf(userId), tenantId, version),
                () -> this.listUserMenuIdsFromDb(userId, tenantId, version));
    }

    /**
     * 获取用户在指定租户下关联的全部菜单（含按钮）
     * <p>
     * menuId 集合来自用户菜单缓存，菜单详情从租户菜单缓存过滤组装。
     * 以租户菜单（租户管理员角色绑定菜单）为白名单过滤，用户菜单仅保留当前租户范围内的部分，
     * 剔除租户范围外的无效菜单（如已从租户菜单解绑、数据不一致等）。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 菜单列表
     */
    public List<Menu> listUserMenus(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return Collections.emptyList();
        }
        List<Long> menuIds = listUserMenuIds(userId, tenantId);
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }

        // 以租户菜单为白名单过滤：仅保留租户范围内的用户菜单
        List<Menu> tenantMenus = tenantMenuAssembleService.listTenantMenus(tenantId);
        if (CollectionUtils.isEmpty(tenantMenus)) {
            return Collections.emptyList();
        }
        Map<Long, Menu> menuMap = tenantMenus.stream()
                .collect(Collectors.toMap(Menu::getMenuId, Function.identity(), (m1, m2) -> m1));

        return menuIds.stream()
                .map(menuMap::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Menu::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 从数据库组装用户 menuId 集合并回写缓存
     */
    private List<Long> listUserMenuIdsFromDb(Long userId, String tenantId, long version) {
        Set<Long> menuIds = new HashSet<>();

        // 用户直接绑定的菜单（category=1）
        Set<Long> directMenuIds = userAuthorityService.listDataIds(userId, tenantId, AuthorityCategoryEnum.MENU.getCode());
        menuIds.addAll(directMenuIds);

        // 用户角色绑定的菜单（category=0 -> role_menu），用户角色缓存优先
        List<Role> userRoles = userRoleAssembleService.listUserRoles(userId, tenantId);
        for (Role role : userRoles) {
            Set<Long> roleMenuIds = roleMenuService.listMenuIdsByRoleId(role.getRoleId());
            menuIds.addAll(roleMenuIds);
        }

        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }

        List<Long> result = menuIds.stream().sorted().collect(Collectors.toList());
        // 回写前校验：回源期间版本号已变化则跳过回写，避免旧数据配新版本号被误命中
        if (version == permissionVersionCache.getVersion(tenantId)) {
            userMenuCache.cacheUserMenuIds(result, String.valueOf(userId), tenantId, version);
        }
        return result;
    }

    /**
     * 获取用户在指定租户下指定应用的菜单树（仅菜单，父子集结构）
     * <p>
     * 以用户菜单数据为基础，按 clientId 过滤出当前应用菜单，并仅保留菜单类型
     * （category=0，剔除按钮），供前端生成应用 router；排序沿用用户菜单的 sort 升序。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param clientId 应用 ID
     * @return 当前应用的菜单树（父子集结构）
     */
    public List<MenuVO> listUserMenuTree(Long userId, String tenantId, String clientId) {
        if (userId == null || StringUtils.isBlank(tenantId) || StringUtils.isBlank(clientId)) {
            return Collections.emptyList();
        }
        List<Menu> menus = listUserMenus(userId, tenantId);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        // 通过 clientId 过滤应用维度，并筛选出菜单类型（category=0），剔除按钮
        List<MenuVO> vos = menus.stream()
                .filter(menu -> clientId.equals(menu.getClientId()))
                .filter(menu -> Integer.valueOf(CATEGORY_MENU).equals(menu.getCategory()))
                .map(menu -> DtoEntityUtil.trans(menu, MenuVO.class))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(vos)) {
            return Collections.emptyList();
        }

        // 按 parentId 分组，递归组装父子集结构
        Map<Long, List<MenuVO>> group = vos.stream()
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? TOP_PARENT_ID : vo.getParentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<MenuVO> roots = group.getOrDefault(TOP_PARENT_ID, new ArrayList<>());
        roots.forEach(root -> buildChildren(root, group));
        return roots;
    }

    /**
     * 递归组装菜单子节点
     */
    private void buildChildren(MenuVO parent, Map<Long, List<MenuVO>> group) {
        List<MenuVO> children = group.get(parent.getMenuId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        parent.setChildren(children);
        children.forEach(child -> buildChildren(child, group));
    }
}
