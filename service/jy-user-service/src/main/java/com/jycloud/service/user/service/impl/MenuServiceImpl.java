package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.UcConstant;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.entity.RoleMenu;
import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.service.user.cache.PermissionVersionCache;
import com.jycloud.service.user.cache.TenantMenuCache;
import com.jycloud.service.user.repository.mapper.MenuMapper;
import com.jycloud.service.user.service.MenuService;
import com.jycloud.service.user.service.RoleMenuService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.DtoEntityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private static final int CATEGORY_MENU = 0;
    private static final int CATEGORY_BUTTON = 1;
    private static final long TOP_PARENT_ID = 0L;

    private final RoleMenuService roleMenuService;
    private final PermissionVersionCache permissionVersionCache;
    private final TenantMenuCache tenantMenuCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Menu create(Menu menu) {
        if (menu == null || StringUtils.isBlank(menu.getClientId()) || StringUtils.isBlank(menu.getMenuTitle())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        fillDefault(menu);
        validateParent(menu.getClientId(), null, menu.getCategory(), menu.getParentId());
        validateCategoryFields(menu);
        this.save(menu);
        return menu;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Menu update(Menu menu) {
        if (menu == null || menu.getMenuId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        if (CATEGORY_BUTTON == menu.getCategory()) {
            menu.setPath(null);
            menu.setIcon(null);
            menu.setComponent(null);
        }

        validateParent(menu.getClientId(), menu.getMenuId(), menu.getCategory(), menu.getParentId());
        validateCategoryFields(menu);

        this.updateById(menu);

        // 菜单（perms）变更影响所有引用该菜单的角色所在租户，逐个失效其权限缓存
        evictPermissionByMenu(menu.getMenuId());

        return this.getById(menu.getMenuId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long menuId) {
        if (menuId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        getMenuOrThrow(menuId);

        long childCount = this.count(new LambdaQueryWrapper<Menu>().eq(Menu::getParentId, menuId));
        if (childCount > 0) {
            throw new SysException("存在子菜单或按钮,请先删除子节点");
        }

        // 失效引用该菜单的租户权限缓存（需在清理角色菜单关联前查询）
        evictPermissionByMenu(menuId);

        // 清理角色菜单关联
        roleMenuService.remove(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getMenuId, menuId));

        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(menuId, deleteUid);
    }

    @Override
    public Menu getMenuById(Long menuId) {
        if (menuId == null) {
            return null;
        }
        return this.getById(menuId);
    }

    @Override
    public List<MenuVO> listMenuTree(String clientId) {
        if (StringUtils.isBlank(clientId)) {
            return new ArrayList<>();
        }
        List<Menu> menus = this.list(new LambdaQueryWrapper<Menu>()
                .eq(Menu::getClientId, clientId)
                .orderByAsc(Menu::getSort));
        if (CollectionUtils.isEmpty(menus)) {
            return new ArrayList<>();
        }

        Map<Long, List<MenuVO>> group = menus.stream()
                .map(menu -> DtoEntityUtil.trans(menu, MenuVO.class))
                .collect(Collectors.groupingBy(
                        vo -> vo.getParentId() == null ? TOP_PARENT_ID : vo.getParentId(),
                        LinkedHashMap::new,
                        Collectors.toList()));

        List<MenuVO> roots = group.getOrDefault(TOP_PARENT_ID, new ArrayList<>());
        roots.forEach(root -> buildChildren(root, group));
        return roots;
    }

    // ======================== 私有方法 ========================

    private void fillDefault(Menu menu) {
        menu.setMenuId(null);
        if (menu.getParentId() == null) {
            menu.setParentId(TOP_PARENT_ID);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getHide() == null) {
            menu.setHide(0);
        }
        if (CATEGORY_BUTTON == menu.getCategory()) {
            menu.setPath(null);
            menu.setIcon(null);
            menu.setComponent(null);
        }
    }

    private void validateCategoryFields(Menu menu) {
        if (menu.getCategory() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        if (CATEGORY_MENU == menu.getCategory()) {

        } else if (CATEGORY_BUTTON == menu.getCategory()) {
            if (StringUtils.isBlank(menu.getPerms())) {
                throw new SysException("按钮必须填写权限标识");
            }
        } else {
            throw new SysException("菜单类型不正确");
        }
    }

    private void validateParent(String clientId, Long menuId, Integer category, Long parentId) {
        long parent = parentId == null ? TOP_PARENT_ID : parentId;
        if (parent == TOP_PARENT_ID) {
            if (category != null && CATEGORY_BUTTON == category) {
                throw new SysException("按钮不能作为顶级菜单");
            }
            return;
        }

        Menu parentMenu = this.getById(parent);
        if (parentMenu == null) {
            throw new SysException("上级菜单不存在");
        }
        if (parentMenu.getCategory() == null || CATEGORY_MENU != parentMenu.getCategory()) {
            throw new SysException("上级节点必须是菜单");
        }
        if (StringUtils.isNotBlank(clientId) && !clientId.equals(parentMenu.getClientId())) {
            throw new SysException("上级菜单不属于当前应用");
        }
        if (menuId != null && menuId.equals(parent)) {
            throw new SysException("上级菜单不能是自身");
        }
    }

    private void buildChildren(MenuVO parent, Map<Long, List<MenuVO>> group) {
        List<MenuVO> children = group.get(parent.getMenuId());
        if (CollectionUtils.isEmpty(children)) {
            return;
        }
        parent.setChildren(children);
        children.forEach(child -> buildChildren(child, group));
    }

    private Menu getMenuOrThrow(Long menuId) {
        Menu menu = this.getById(menuId);
        if (menu == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }
        return menu;
    }

    /**
     * 菜单变更后，失效所有引用该菜单的角色所在租户的权限缓存
     */
    private void evictPermissionByMenu(Long menuId) {
        Set<String> tenantIds = roleMenuService.listTenantIdsByMenuId(menuId);
        if (CollectionUtils.isEmpty(tenantIds)) {
            return;
        }
        for (String tenantId : tenantIds) {
            tenantMenuCache.delTenantMenus(tenantId);
            permissionVersionCache.incr(tenantId);
        }
    }
}
