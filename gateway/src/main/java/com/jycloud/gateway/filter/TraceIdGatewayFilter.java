package com.jycloud.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.jycloud.tool.common.constant.HeaderConstant.REQUEST_ID;
import static com.jycloud.tool.common.constant.HeaderConstant.TRACE_ID;

@Component
public class TraceIdGatewayFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 优先从上游获取，没有则生成
        String traceId = exchange.getRequest().getHeaders().getFirst(TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        // 2. 构造新请求头（响应式不可变对象，必须 mutate）
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header(TRACE_ID, traceId)
                .header(REQUEST_ID, traceId) // 兼容老系统
                .build();

        // 3. 将 traceId 存入 Exchange 属性，供 Gateway 自身日志/监控使用
        exchange.getAttributes().put("traceId", traceId);

        // 4. 在响应提交前追加 Header（提交后 Header 变为只读，不能再 add）
        String finalTraceId = traceId;
        exchange.getResponse().beforeCommit(() -> {
            exchange.getResponse().getHeaders().set(TRACE_ID, finalTraceId);
            exchange.getResponse().getHeaders().set(REQUEST_ID, finalTraceId);
            return Mono.empty();
        });

        // 5. 执行下游路由
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1; // 确保在其他过滤器之前执行
    }
}