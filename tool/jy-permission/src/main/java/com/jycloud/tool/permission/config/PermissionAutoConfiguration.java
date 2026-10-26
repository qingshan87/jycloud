package com.jycloud.tool.permission.config;

import com.jycloud.tool.permission.aspect.PermissionAspect;
import com.jycloud.tool.permission.aspect.RoleAspect;
import com.jycloud.tool.permission.spi.PermissionProvider;
import com.jycloud.tool.permission.spi.RoleProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * 权限模块自动装配
 * <p>
 * 当 Spring 容器中存在 {@link PermissionProvider} 的实现类时才创建切面。
 * 各微服务需要自行注入 {@link PermissionProvider} 的实现：
 * <ul>
 *   <li>jy-user-service：注入缓存优先的 {@code UserRoleProvider / UserPermissionProvider}（本地实现）</li>
 *   <li>jy-business：注入通过 HttpExchange 远程调用的 {@code RemotePermissionProvider}</li>
 * </ul>
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@AutoConfiguration
public class PermissionAutoConfiguration {

    /**
     * 仅在容器中存在 PermissionProvider 实现时才创建切面。
     * 如果某个服务不需要权限校验，不注入 PermissionProvider 即可，切面不会生效。
     */
    @Bean
    @ConditionalOnBean(PermissionProvider.class)
    public PermissionAspect permissionAspect(PermissionProvider permissionProvider) {
        return new PermissionAspect(permissionProvider);
    }

    /**
     * 仅在容器中存在 RoleProvider 实现时才创建角色校验切面。
     * 如果某个服务不需要角色校验，不注入 RoleProvider 即可，切面不会生效。
     */
    @Bean
    @ConditionalOnBean(RoleProvider.class)
    public RoleAspect roleAspect(RoleProvider roleProvider) {
        return new RoleAspect(roleProvider);
    }
}
