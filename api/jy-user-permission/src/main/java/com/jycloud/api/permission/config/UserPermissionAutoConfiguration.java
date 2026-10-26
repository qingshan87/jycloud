package com.jycloud.api.permission.config;

import com.jycloud.api.permission.provider.RemoteUserPermissionProvider;
import com.jycloud.api.permission.provider.RemoteUserRoleProvider;
import com.jycloud.api.user.client.UserAuthorityClient;
import com.jycloud.api.user.config.UserAuthorityClientAutoConfiguration;
import com.jycloud.tool.permission.config.PermissionAutoConfiguration;
import com.jycloud.tool.permission.spi.PermissionProvider;
import com.jycloud.tool.permission.spi.RoleProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 业务服务远程权限桥接自动配置。
 *
 * <p>业务应用引入 {@code jy-user-permission} 后即可使用
 * {@code @RequirePermission} 和 {@code @RequireRole}。两个 Provider 均可被业务侧
 * 自定义 Bean 覆盖，用户服务自身的本地 Provider 会优先保留。</p>
 */
@AutoConfiguration(
        after = UserAuthorityClientAutoConfiguration.class,
        before = PermissionAutoConfiguration.class)
@ConditionalOnClass({UserAuthorityClient.class, PermissionProvider.class, RoleProvider.class})
@ConditionalOnBean(UserAuthorityClient.class)
public class UserPermissionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PermissionProvider.class)
    public PermissionProvider permissionProvider(UserAuthorityClient userAuthorityClient) {
        return new RemoteUserPermissionProvider(userAuthorityClient);
    }

    @Bean
    @ConditionalOnMissingBean(RoleProvider.class)
    public RoleProvider roleProvider(UserAuthorityClient userAuthorityClient) {
        return new RemoteUserRoleProvider(userAuthorityClient);
    }
}
