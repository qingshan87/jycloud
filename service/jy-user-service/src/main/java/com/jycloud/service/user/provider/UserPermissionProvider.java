package com.jycloud.service.user.provider;

import com.jycloud.service.user.assemble.UserPermissionAssembleService;
import com.jycloud.tool.permission.spi.PermissionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Set;

/**
 * 用户权限提供者 缓存优先实现
 * <p>
 * 通过用户权限聚合服务（缓存优先）获取用户在该租户下的权限标识集合（perms），
 * 供 {@code @RequirePermission} 切面鉴权使用，缓存命中时零数据库访问。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPermissionProvider implements PermissionProvider {

    private final UserPermissionAssembleService userPermissionAssembleService;

    @Override
    public Set<String> getPermissions(String userId, String tenantId) {
        if (!StringUtils.hasText(userId)) {
            return Collections.emptySet();
        }

        Long uid;
        try {
            uid = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            log.warn("【权限查询】非法 userId: {}", userId);
            return Collections.emptySet();
        }

        return userPermissionAssembleService.listPermissions(uid, tenantId);
    }
}
