package com.jycloud.service.user.assemble;

import com.jycloud.api.user.entity.Menu;
import com.jycloud.service.user.cache.PermissionVersionCache;
import com.jycloud.service.user.cache.UserPermissionCache;
import com.jycloud.tool.common.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限标识聚合服务
 * <p>
 * 负责从用户关联的菜单（用户直接绑定 + 角色绑定）中提取权限标识（perms）集合，
 * 供权限校验切面使用。缓存读取时与租户权限版本号比对，版本不一致则回源重算并回写缓存。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPermissionAssembleService {

    private final UserMenuAssembleService userMenuAssembleService;
    private final UserPermissionCache userPermissionCache;
    private final PermissionVersionCache permissionVersionCache;

    /**
     * 获取用户在指定租户下的完整权限标识集合（perms）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 权限标识集合；无权限时返回空集合
     */
    public Set<String> listPermissions(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return Collections.emptySet();
        }
        long version = permissionVersionCache.getVersion(tenantId);
        return CommonUtil.selectCacheByTemplate(
                () -> userPermissionCache.getPermissionsByUserId(String.valueOf(userId), tenantId, version),
                () -> this.listPermissionsFromDb(userId, tenantId, version));
    }

    /**
     * 从数据库组装用户权限标识集合并回写缓存
     */
    private Set<String> listPermissionsFromDb(Long userId, String tenantId, long version) {
        // 复用用户菜单组装逻辑获取菜单详情（已按租户菜单白名单过滤无效菜单）
        List<Menu> menus = userMenuAssembleService.listUserMenus(userId, tenantId);
        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptySet();
        }

        Set<String> perms = menus.stream()
                .map(Menu::getPerms)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        // 回写前校验：回源期间版本号已变化则跳过回写，避免旧数据配新版本号被误命中
        if (version == permissionVersionCache.getVersion(tenantId)) {
            userPermissionCache.cachePermissions(perms, String.valueOf(userId), tenantId, version);
        }
        return perms;
    }
}
