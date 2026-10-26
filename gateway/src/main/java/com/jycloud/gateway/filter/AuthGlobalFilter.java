package com.jycloud.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.jycloud.gateway.properties.AuthProperties;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.TokenConstant;
import com.jycloud.tool.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static com.jycloud.api.user.constant.UcConstant.USER_TOKEN_CACHE_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final AuthProperties authProperties;
    private final RedisCacheHelper cacheHelper;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // 文档路径正则规则（支持任意服务名前缀，新增服务无需改代码）
    private static final Pattern DOC_PATTERN = Pattern.compile(
            "^/[^/]+/(v3/api-docs(/.*)?|doc\\.html|swagger-ui(/.*)?|webjars/.*|swagger-resources/.*)$"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!authProperties.isEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        if (authProperties.getWhiteList() != null
                && authProperties.getWhiteList().stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            log.debug("匹配认证白名单: {}", path);
            return chain.filter(exchange);
        }

        // 自动放行所有微服务的文档路径（正则匹配）
        if (DOC_PATTERN.matcher(path).matches()) {
            log.debug("自动放行文档接口请求: {}", path);
            return chain.filter(exchange);
        }

        // 鉴权逻辑：检查 Token
        String token = request.getHeaders().getFirst(TokenConstant.TOKEN);
        if (StringUtils.isBlank(token)) {
            log.warn("拦截未授权请求(缺少令牌): {}", path);
            return writeUnauthorized(exchange, ResponseCode.UNAUTHORIZED.getMessage());
        }

        String decryptedToken = JWTUtil.decryptToken(token);
        if (StringUtils.isBlank(decryptedToken)) {
            log.warn("拦截非法令牌请求: {}", path);
            return writeUnauthorized(exchange, "非法令牌，请重新登录");
        }

        String userId = JWTUtil.getUserId(decryptedToken);
        if (StringUtils.isBlank(userId)) {
            log.warn("拦截缺少用户信息的令牌请求: {}", path);
            return writeUnauthorized(exchange, "令牌用户信息无效，请重新登录");
        }
        if (isTokenExpired(userId, token)) {
            log.warn("拦截过期或失效令牌请求: {}", path);
            return writeUnauthorized(exchange, "令牌已失效，请重新登录");
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }

    private boolean isTokenExpired(String userId, String encryptedToken) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(encryptedToken)) {
            return true;
        }
        String key = USER_TOKEN_CACHE_PREFIX + userId;
        String cachedToken = normalizeCachedToken(this.cacheHelper.get(key));
        return !encryptedToken.equals(cachedToken);
    }

    /**
     * 兼容旧系统将字符串 JSON 序列化后写入缓存（带首尾双引号）的格式。
     */
    private String normalizeCachedToken(String cachedToken) {
        if (cachedToken == null || cachedToken.length() < 2
                || cachedToken.charAt(0) != '"'
                || cachedToken.charAt(cachedToken.length() - 1) != '"') {
            return cachedToken;
        }
        try {
            Object parsed = JSON.parse(cachedToken);
            return parsed instanceof String value ? value : cachedToken;
        } catch (Exception e) {
            log.warn("缓存 Token 为 JSON 字符串字面量但解析失败，按原值比对");
            return cachedToken;
        }
    }

    private Mono<Void> writeUnauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(new IllegalStateException("响应已提交，无法写入401"));
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = JSON.toJSONString(
                ServerResponse.errorCodeMsg(ResponseCode.UNAUTHORIZED.getCode(), message));
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            return bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8));
        }));
    }
}
