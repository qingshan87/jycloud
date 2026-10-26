package com.jycloud.api.user.client;

import com.jycloud.tool.common.api.ServerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class UserAuthorityClientTest {

    @Test
    void shouldCallHttpExchangeEndpoint() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://jy-user");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        UserAuthorityClient client = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(builder.build()))
                .build()
                .createClient(UserAuthorityClient.class);

        server.expect(requestTo("http://jy-user/client/user-authority/roles?userId=1&tenantId=000000"))
                .andRespond(withSuccess(
                        "{\"code\":200,\"msg\":\"success\",\"data\":[\"admin\"]}",
                        MediaType.APPLICATION_JSON));

        ServerResponse<Set<String>> response = client.getRoles("1", "000000");

        assertTrue(ServerResponse.isSuccess(response));
        assertEquals(Set.of("admin"), response.getData());
        server.verify();
    }
}
