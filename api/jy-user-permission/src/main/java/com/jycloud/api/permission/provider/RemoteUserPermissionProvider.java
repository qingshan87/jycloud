package com.jycloud.api.permission.provider;

import com.jycloud.api.user.client.UserAuthorityClient;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.permission.spi.PermissionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 基于 HttpExchange 的远程权限提供者。
 *
 * <p>远程调用失败、响应失败或数据为空时返回空集合，由权限切面拒绝访问。</p>
 */
@Slf4j
@RequiredArgsConstructor
public class RemoteUserPermissionProvider implements PermissionProvider {

    private final UserAuthorityClient userAuthorityClient;

    @Override
    public Set<String> getPermissions(String userId, String tenantId) {
        if (!StringUtils.hasText(userId) || !StringUtils.hasText(tenantId)) {
            return Set.of();
        }
        try {
            ServerResponse<Set<String>> response = userAuthorityClient.getPermissions(userId, tenantId);
            if (!ServerResponse.isSuccess(response) || response.getData() == null) {
                return Set.of();
            }
            return response.getData().stream()
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toUnmodifiableSet());
        } catch (RuntimeException e) {
            log.error("远程查询用户权限失败, userId={}, tenantId={}", userId, tenantId, e);
            return Set.of();
        }
    }
}
