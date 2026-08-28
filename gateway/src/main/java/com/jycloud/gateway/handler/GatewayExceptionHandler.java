package com.jycloud.gateway.handler;

import com.alibaba.fastjson2.JSON;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway 全局异常处理器（WebFlux 响应式栈）
 * <p>
 * 替代 @RestControllerAdvice —— Gateway 运行在 Netty + WebFlux 上，
 * Servlet 栈的 @RestControllerAdvice 在此无效。
 * 本处理器拦截 Gateway Filter / Predicate 中所有未捕获异常，
 * 统一转换为 ServerResponse JSON 格式返回。
 * </p>
 *
 * @see GlobalExceptionHandler 服务层异常处理器（仅 Servlet 微服务有效）
 */
@Slf4j
@Component
@Order(-2) // 优先级高于默认的 DefaultErrorWebExceptionHandler（-1）
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerResponse<Void> result;
        HttpStatus httpStatus;

        if (ex instanceof SysException sysEx) {
            // 业务异常：返回业务错误码
            log.warn("【Gateway 业务异常】code={}, msg={}, path={}",
                    sysEx.getCode(), sysEx.getMessage(), exchange.getRequest().getPath());

            int code = sysEx.getCode() != null
                    ? sysEx.getCode()
                    : ResponseCode.SYSTEM_INTERNAL_ERROR.getCode();
            result = ServerResponse.errorCodeMsg(code, sysEx.getMessage());
            httpStatus = mapCodeToHttpStatus(code);
        } else if (ex instanceof NoResourceFoundException) {
            // 静态资源缺失（如浏览器自动请求 /favicon.ico），属预期内噪音，
            // 静默返回 404，不打印堆栈
            log.debug("【Gateway 静态资源不存在】path={}", exchange.getRequest().getPath());
            result = ServerResponse.errorCodeMsg(HttpStatus.NOT_FOUND.value(), "资源不存在");
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof ResponseStatusException statusEx) {
            // 其他带 HTTP 状态码的异常（如 MethodNotAllowedException）
            HttpStatus status = HttpStatus.resolve(statusEx.getStatusCode().value());
            if (status == null) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            log.warn("【Gateway HTTP 状态异常】status={}, path={}",
                    status, exchange.getRequest().getPath());
            result = ServerResponse.errorCodeMsg(status.value(), status.getReasonPhrase());
            httpStatus = status;
        } else {
            // 系统异常：记录完整堆栈，生产环境隐藏详情
            log.error("【Gateway 系统异常】path={}", exchange.getRequest().getPath(), ex);
            result = ServerResponse.errorResponseCode(ResponseCode.SYSTEM_INTERNAL_ERROR);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(httpStatus);

        byte[] bytes = JSON.toJSONBytes(result);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    /**
     * 将业务错误码映射到对应的 HTTP 状态码
     * <p>
     * 注意：非 HTTP 语义的业务码（如 1001、2001 等）统一返回 200 OK，
     * 由响应 body 中的 code 字段区分具体错误类型，避免误导 HTTP 监控。
     */
    private HttpStatus mapCodeToHttpStatus(int code) {
        return switch (code) {
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 403 -> HttpStatus.FORBIDDEN;
            case 404 -> HttpStatus.NOT_FOUND;
            case 429 -> HttpStatus.TOO_MANY_REQUESTS;
            default -> HttpStatus.OK;
        };
    }
}
