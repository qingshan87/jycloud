package com.jycloud.service.user.assemble;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycloud.api.user.constant.AdminRoleNameEnum;
import com.jycloud.api.user.constant.UcConstant;
import com.jycloud.api.user.entity.Client;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.api.user.vo.TenantClientVO;
import com.jycloud.service.user.cache.TenantMenuCache;
import com.jycloud.service.user.service.ClientService;
import com.jycloud.service.user.service.MenuService;
import com.jycloud.service.user.service.RoleMenuService;
import com.jycloud.service.user.service.RoleService;
import com.jycloud.service.user.service.TenantClientService;
import com.jycloud.tool.common.util.CommonUtil;
import com.jycloud.tool.common.util.DtoEntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    private static final long TOP_PARENT_ID = 0L;

    private final RoleService roleService;
    private final RoleMenuService roleMenuService;
    private final MenuService menuService;
    private final TenantMenuCache tenantMenuCache;
    private final TenantClientService tenantClientService;
    private final ClientService clientService;

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
     * 获取租户全部菜单树（含按钮，角色分配菜单用）
     *
     * @param tenantId 租户 ID
     * @return 菜单树
     */
    public List<MenuVO> listTenantMenuTree(String tenantId) {
        List<Menu> menus = this.listTenantMenus(tenantId);
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }
        List<MenuVO> vos = menus.stream()
                .map(menu -> DtoEntityUtil.trans(menu, MenuVO.class))
                .collect(Collectors.toList());
        return buildTree(vos);
    }

    /**
     * 获取当前租户按应用分类的菜单树（含按钮）
     * <p>
     * 外层为租户绑定的应用列表（{@link TenantClientVO}），用户中心（1112）为默认应用，
     * 未在绑定列表中时自动补充；每个应用的 menuList 为该应用在租户全量菜单
     * （租户管理员角色绑定菜单）范围内的菜单树，供角色菜单分配、用户资源分配按应用维度勾选。
     * 仅返回存在菜单的应用，避免空应用占用分配空间。
     * </p>
     *
     * @param tenantId 租户 ID
     * @return 应用分组菜单树列表
     */
    public List<TenantClientVO> listTenantClientMenuTree(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return new ArrayList<>();
        }

        // 1. 租户绑定的应用
        List<TenantClientVO> clients = tenantClientService.listByTenant(tenantId);
        if (CollectionUtils.isEmpty(clients)) {
            return new ArrayList<>();
        }

        // 2. 租户全量菜单按 clientId 分组（菜单已按 sort 升序，分组保序；含按钮）
        List<MenuVO> menuVOS = this.listTenantMenuTree(tenantId);
        Map<String, List<MenuVO>> menuMap = new LinkedHashMap<>();
        if (!CollectionUtils.isEmpty(menuVOS)){
            menuMap = menuVOS.stream()
                    .collect(Collectors.groupingBy(
                            MenuVO::getClientId,
                            LinkedHashMap::new,
                            Collectors.toList()));
        }

        // 3. 按应用填充菜单树
        for (TenantClientVO client : clients) {
            List<MenuVO> clientMenus = menuMap.get(client.getClientId());
            client.setMenuList(buildTree(clientMenus));
        }
        return clients;
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

    /**
     * 组装默认用户中心应用外壳（无 t_tenant_client 绑定记录）
     */
    private TenantClientVO buildDefaultUcenterClient(String tenantId) {
        TenantClientVO vo = new TenantClientVO();
        vo.setTenantId(tenantId);
        vo.setClientId(UcConstant.UCENTER_CLIENT_ID);
        vo.setStatus(1);
        Client client = clientService.getClientByClientId(UcConstant.UCENTER_CLIENT_ID);
        if (client != null) {
            vo.setAppName(client.getAppName());
            vo.setCategory(client.getCategory());
            vo.setIcon(client.getIcon());
            vo.setDescription(client.getDescription());
            vo.setWebsite(client.getWebsite());
            vo.setClientStatus(client.getStatus());
        }
        return vo;
    }

    /**
     * 按 parentId 分组组装父子集结构
     */
    private List<MenuVO> buildTree(List<MenuVO> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return new ArrayList<>();
        }
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
     * 构建菜单树
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
