package com.jycloud.gateway.filter;

import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.constant.TokenConstant;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    // 固定业务白名单
    private static final List<String> WHITE_LIST = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/error",
            "/favicon.ico"
    );

    // 文档路径正则规则（支持任意服务名前缀，新增服务无需改代码）
    private static final Pattern DOC_PATTERN = Pattern.compile(
            "^/[^/]+/(v3/api-docs(/.*)?|doc\\.html|swagger-ui(/.*)?|webjars/.*|swagger-resources/.*)$"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 匹配固定业务白名单
        if (WHITE_LIST.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }

        // 自动放行所有微服务的文档路径（正则匹配）
        if (DOC_PATTERN.matcher(path).matches()) {
            log.debug("自动放行文档接口请求: {}", path);
            return chain.filter(exchange);
        }

        // 鉴权逻辑：检查 Token
        String token = exchange.getRequest().getHeaders().getFirst(TokenConstant.TOKEN);
        if (token == null || token.isEmpty() || JWTUtil.decryptToken(token) == null) {
            log.warn("拦截未授权请求: {}", path);
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        String decryptedToken = JWTUtil.decryptToken(token);
        String userId = JWTUtil.getUserId(decryptedToken);
        String username = JWTUtil.getUsername(decryptedToken);
        String tenantId = JWTUtil.getTenantId(decryptedToken);

        // 透传租户/用户头（原有逻辑保持不变）
        ServerWebExchange mutated = exchange.mutate()
                .request(r -> r.header("X-User-ID", userId))
                .request(r -> r.header("X-Username", username))
                .request(r -> r.header("X-Tenant-ID", tenantId != null ? tenantId : "0"))
                .build();

        return chain.filter(mutated);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}