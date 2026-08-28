package com.jycloud.service.user.assemble;

import com.jycloud.api.user.constant.AuthorityCategoryEnum;
import com.jycloud.api.user.entity.Role;
import com.jycloud.service.user.cache.PermissionVersionCache;
import com.jycloud.service.user.cache.UserRoleCache;
import com.jycloud.service.user.service.RoleService;
import com.jycloud.service.user.service.UserAuthorityService;
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
 * 用户角色聚合服务
 * <p>
 * 负责组装用户在指定租户下关联的启用角色列表（category=0 -> t_user_authority -> t_role），
 * 缓存优先读取，缓存未命中或版本不一致时回源 DB 并回写缓存。
 * 角色以 Role 对象入缓存，用户菜单组装与鉴权角色查询（{@link com.jycloud.service.user.provider.UserRoleProvider}）均可全缓存命中。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRoleAssembleService {

    private final UserAuthorityService userAuthorityService;
    private final RoleService roleService;
    private final UserRoleCache userRoleCache;
    private final PermissionVersionCache permissionVersionCache;

    /**
     * 获取用户在指定租户下关联的启用角色列表
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 角色列表；无角色时返回空列表
     */
    public List<Role> listUserRoles(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return Collections.emptyList();
        }
        long version = permissionVersionCache.getVersion(tenantId);
        return CommonUtil.selectCacheByTemplate(
                () -> userRoleCache.getUserRolesByUserId(String.valueOf(userId), tenantId, version),
                () -> this.listUserRolesFromDb(userId, tenantId, version));
    }

    /**
     * 从数据库组装用户角色列表并回写缓存
     */
    private List<Role> listUserRolesFromDb(Long userId, String tenantId, long version) {
        Set<Long> roleIds = userAuthorityService.listDataIds(userId, tenantId, AuthorityCategoryEnum.ROLE.getCode());
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 仅保留启用角色（delete_flag 由逻辑删除自动过滤）
        List<Role> roles = roleService.listByIds(roleIds).stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        // 回写前校验：回源期间版本号已变化则跳过回写，避免旧数据配新版本号被误命中
        if (version == permissionVersionCache.getVersion(tenantId)) {
            userRoleCache.cacheUserRoles(roles, String.valueOf(userId), tenantId, version);
        }
        return roles;
    }
}
