package com.jycloud.gateway.filter;

import com.jycloud.gateway.properties.AuthProperties;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import com.jycloud.tool.common.constant.TokenConstant;
import com.jycloud.tool.common.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static com.jycloud.api.user.constant.UcConstant.USER_TOKEN_CACHE_PREFIX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthGlobalFilterTest {

    private final RedisCacheHelper cacheHelper = mock(RedisCacheHelper.class);
    private final GatewayFilterChain chain = mock(GatewayFilterChain.class);

    private AuthGlobalFilter filter;

    @BeforeEach
    void setUp() {
        when(chain.filter(any())).thenReturn(Mono.empty());
        filter = new AuthGlobalFilter(new AuthProperties(), cacheHelper);
    }

    @Test
    void shouldReturnJson401WhenTokenIsMissing() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/jy-user/system/users").build());

        filter.filter(exchange, chain).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        String body = exchange.getResponse().getBodyAsString().block();
        assertTrue(body != null && body.contains("\"code\":401"));
    }

    @Test
    void shouldAcceptTokenStoredAsQuotedJsonString() {
        String token = createToken("1");
        when(cacheHelper.get(USER_TOKEN_CACHE_PREFIX + "1")).thenReturn("\"" + token + "\"");
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/jy-user/system/users")
                        .header(TokenConstant.TOKEN, token)
                        .build());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void shouldPassCorsPreflightWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.options("/jy-user/system/users").build());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void shouldPassConfiguredWhiteListWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/jy-user/system/auth/login").build());

        filter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    private static String createToken(String userId) {
        String rawToken = JWTUtil.createToken("admin", userId, "000000");
        return JWTUtil.encryptToken(rawToken);
    }

}
