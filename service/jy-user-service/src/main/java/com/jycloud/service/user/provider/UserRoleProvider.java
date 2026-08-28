package com.jycloud.service.user.provider;

import com.jycloud.api.user.entity.Role;
import com.jycloud.service.user.assemble.UserRoleAssembleService;
import com.jycloud.service.user.cache.UserRoleCache;
import com.jycloud.tool.permission.spi.RoleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户角色提供者 缓存优先实现
 * <p>
 * 优先从用户角色缓存（{@link UserRoleCache}）读取用户在指定租户下的启用角色列表，
 * 缓存未命中或版本不一致时由 {@link UserRoleAssembleService} 回源并回写缓存，
 * 直接提取 role_name 返回角色标识集合，缓存命中时零数据库访问。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRoleProvider implements RoleProvider {

    private final UserRoleAssembleService userRoleAssembleService;

    @Override
    public Set<String> getRoles(String userId, String tenantId) {
        if (!StringUtils.hasText(userId)) {
            return Collections.emptySet();
        }

        Long uid;
        try {
            uid = Long.valueOf(userId);
        } catch (NumberFormatException e) {
            log.warn("【角色查询】非法 userId: {}", userId);
            return Collections.emptySet();
        }

        // 优先缓存查找：用户角色缓存命中则直接提取角色名，零数据库访问
        List<Role> roles = userRoleAssembleService.listUserRoles(uid, tenantId);
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptySet();
        }
        return roles.stream()
                .map(Role::getRoleName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }
}
