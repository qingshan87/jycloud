package com.jycloud.api.permission.config;

import com.jycloud.api.permission.provider.RemoteUserPermissionProvider;
import com.jycloud.api.permission.provider.RemoteUserRoleProvider;
import com.jycloud.api.user.client.UserAuthorityClient;
import com.jycloud.tool.permission.aspect.PermissionAspect;
import com.jycloud.tool.permission.aspect.RoleAspect;
import com.jycloud.tool.permission.config.PermissionAutoConfiguration;
import com.jycloud.tool.permission.spi.PermissionProvider;
import com.jycloud.tool.permission.spi.RoleProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class UserPermissionAutoConfigurationTest {

    private final UserAuthorityClient client = mock(UserAuthorityClient.class);

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(UserPermissionAutoConfiguration.class))
            .withBean(UserAuthorityClient.class, () -> client);

    @Test
    void shouldRegisterRemoteProviders() {
        contextRunner.run(context -> {
            assertInstanceOf(RemoteUserPermissionProvider.class, context.getBean(PermissionProvider.class));
            assertInstanceOf(RemoteUserRoleProvider.class, context.getBean(RoleProvider.class));
        });
    }

    @Test
    void shouldKeepApplicationProvidedProviders() {
        PermissionProvider permissionProvider = (userId, tenantId) -> Set.of("custom:permission");
        RoleProvider roleProvider = (userId, tenantId) -> Set.of("custom_role");

        contextRunner
                .withBean(PermissionProvider.class, () -> permissionProvider)
                .withBean(RoleProvider.class, () -> roleProvider)
                .run(context -> {
                    assertSame(permissionProvider, context.getBean(PermissionProvider.class));
                    assertSame(roleProvider, context.getBean(RoleProvider.class));
                });
    }

    @Test
    void shouldEnablePermissionAspects() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        UserPermissionAutoConfiguration.class,
                        PermissionAutoConfiguration.class))
                .withBean(UserAuthorityClient.class, () -> client)
                .run(context -> {
                    assertInstanceOf(PermissionAspect.class, context.getBean(PermissionAspect.class));
                    assertInstanceOf(RoleAspect.class, context.getBean(RoleAspect.class));
                });
    }
}
