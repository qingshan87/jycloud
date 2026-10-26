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

class RemoteUserRoleProviderTest {

    private final UserAuthorityClient client = mock(UserAuthorityClient.class);
    private final RemoteUserRoleProvider provider = new RemoteUserRoleProvider(client);

    @Test
    void shouldFilterBlankRoles() {
        when(client.getRoles("1", "000000")).thenReturn(
                ServerResponse.successData(new LinkedHashSet<>(
                        java.util.Arrays.asList("admin", "admin", "", null))));

        assertEquals(Set.of("admin"), provider.getRoles("1", "000000"));
    }

    @Test
    void shouldFailClosedWhenRemoteResponseIsNotSuccessful() {
        when(client.getRoles("1", "000000")).thenReturn(ServerResponse.errorMsg("denied"));

        assertEquals(Set.of(), provider.getRoles("1", "000000"));
    }

    @Test
    void shouldFailClosedWhenRemoteCallThrows() {
        when(client.getRoles("1", "000000")).thenThrow(new IllegalStateException("unavailable"));

        assertEquals(Set.of(), provider.getRoles("1", "000000"));
    }

    @Test
    void shouldNotCallRemoteForBlankParameters() {
        assertEquals(Set.of(), provider.getRoles("", "000000"));
        assertEquals(Set.of(), provider.getRoles("1", ""));
        verifyNoInteractions(client);
    }
}
