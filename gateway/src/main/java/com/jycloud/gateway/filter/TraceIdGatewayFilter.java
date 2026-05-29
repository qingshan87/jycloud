package com.jycloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 优先从上游获取，没有则生成
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID_HEADER);
        if (traceId == null || traceId.isBlank()) {
            GENERATOR
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 2. 构造新请求头（响应式不可变对象，必须 mutate）
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(TRACE_ID_HEADER, traceId)
                .header(REQUEST_ID_HEADER, traceId) // 兼容老系统
                .build();

        // 3. 将 traceId 存入 Exchange 属性，供 Gateway 自身日志/监控使用
        exchange.getAttributes().put("traceId", traceId);

        // 4. 执行下游路由 + 响应后追加 Header
        String finalTraceId = traceId;
        return chain.filter(exchange.mutate().request(mutatedRequest).build())
                .then(Mono.fromRunnable(() -> {
                    exchange.getResponse().getHeaders().add(TRACE_ID_HEADER, finalTraceId);
                    exchange.getResponse().getHeaders().add(REQUEST_ID_HEADER, finalTraceId);
                }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1; // 确保在其他过滤器之前执行
    }
}