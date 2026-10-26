package com.jycloud.tool.permission.spi;

import java.util.Collections;
import java.util.Set;

/**
 * 权限提供者 SPI（Service Provider Interface）
 * <p>
 * 各微服务根据自身情况实现：
 * <ul>
 *   <li>jy-ucenter：直接查本地 DB（t_user_authority → t_role_menu → menu.perms）</li>
 *   <li>jy-business：通过 HttpExchange 声明式 HTTP 客户端远程调用 jy-user</li>
 * </ul>
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@FunctionalInterface
public interface PermissionProvider {

    /**
     * 获取用户在该租户下的完整权限标识集合
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 权限标识集合，如 {"system:user:add", "business:order:view"}，无权限时返回空集合
     */
    Set<String> getPermissions(String userId, String tenantId);

    /**
     * 默认空实现 —— 若服务未注入自定义 Provider，切面不会拦截
     */
    static PermissionProvider empty() {
        return (userId, tenantId) -> Collections.emptySet();
    }
}
