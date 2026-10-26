package com.jycloud.tool.permission.spi;

import java.util.Collections;
import java.util.Set;

/**
 * 角色提供者 SPI（Service Provider Interface）
 * <p>
 * 各微服务根据自身情况实现：
 * <ul>
 *   <li>jy-user-service：直接查本地 DB（t_user_authority(category=0) → t_role.role_name）</li>
 *   <li>其他服务：通过 HttpExchange 声明式 HTTP 客户端远程调用</li>
 * </ul>
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@FunctionalInterface
public interface RoleProvider {

    /**
     * 获取用户在该租户下的角色标识集合
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 角色标识集合，如 {"super_admin", "tenant_admin"}，无角色时返回空集合
     */
    Set<String> getRoles(String userId, String tenantId);

    /**
     * 默认空实现 —— 若服务未注入自定义 Provider，切面不会拦截
     */
    static RoleProvider empty() {
        return (userId, tenantId) -> Collections.emptySet();
    }
}
