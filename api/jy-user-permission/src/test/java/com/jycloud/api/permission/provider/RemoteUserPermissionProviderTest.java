package com.jycloud.api.permission.provider;

import com.jycloud.api.user.client.UserAuthorityClient;
import com.jycloud.tool.common.api.ServerResponse;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RemoteUserPermissionProviderTest {

    private final UserAuthorityClient client = mock(UserAuthorityClient.class);
    private final RemoteUserPermissionProvider provider = new RemoteUserPermissionProvider(client);

    @Test
    void shouldFilterBlankPermissions() {
        when(client.getPermissions("1", "000000")).thenReturn(
                ServerResponse.successData(new LinkedHashSet<>(
                        java.util.Arrays.asList("system:user:view", "system:user:view", " ", null))));

        assertEquals(Set.of("system:user:view"), provider.getPermissions("1", "000000"));
    }

    @Test
    void shouldFailClosedWhenRemoteResponseIsNotSuccessful() {
        when(client.getPermissions("1", "000000")).thenReturn(ServerResponse.errorMsg("denied"));

        assertEquals(Set.of(), provider.getPermissions("1", "000000"));
    }

    @Test
    void shouldFailClosedWhenRemoteCallThrows() {
        when(client.getPermissions("1", "000000")).thenThrow(new IllegalStateException("unavailable"));

        assertEquals(Set.of(), provider.getPermissions("1", "000000"));
    }

    @Test
    void shouldNotCallRemoteForBlankParameters() {
        assertEquals(Set.of(), provider.getPermissions("", "000000"));
        assertEquals(Set.of(), provider.getPermissions("1", ""));
        verifyNoInteractions(client);
    }
}
