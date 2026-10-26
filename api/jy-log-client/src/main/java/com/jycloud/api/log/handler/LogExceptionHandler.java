package com.jycloud.api.log.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.utils.SysLogUtil;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.handler.GlobalExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 —— 在 jg-common {@link GlobalExceptionHandler} 的基础上，
 * 额外发布服务异常事件（写错误日志）。
 *
 * <p>经 {@code LogClientAutoConfiguration} 注册为 Bean（Servlet Web 环境生效）；
 * 优先级 HIGHEST_PRECEDENCE + 1，先于 jg-common 默认处理器（HIGHEST_PRECEDENCE + 2）拦截。</p>
 *
 * @author Lqs
 * @date 2022/7/26 16:55
 */
@Slf4j
@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class LogExceptionHandler extends GlobalExceptionHandler {

    private final SysLogUtil sysLogUtil;

    /**
     * 兜底异常处理：先发布服务异常事件，再委托父类统一响应。
     *
     * <p>必须重写父类同名方法（{@code handleGlobalException}）而不要新增别的
     * {@code @ExceptionHandler(Exception.class)} 方法：Spring 7 的
     * {@code ExceptionHandlerMethodResolver} 对“同一异常类型 + 同一媒体类型”
     * 出现两个不同处理方法时会抛 {@code IllegalStateException}（Ambiguous），
     * 重写可保证该异常类型仅保留一个最具体方法。</p>
     */
    @Override
    @ExceptionHandler(value = Exception.class)
    public ServerResponse handleGlobalException(Exception e) {
        sysLogUtil.publishLogErrorEvent(e);
        return super.handleGlobalException(e);
    }

    /**
     * 业务异常处理：先发布服务异常事件，再委托父类统一响应。
     */
    @Override
    @ExceptionHandler(value = SysException.class)
    public ServerResponse handleSysException(SysException e) {
        sysLogUtil.publishLogErrorEvent(e);
        return super.handleSysException(e);
    }

    /**
     * 兜底 Error（如 OutOfMemoryError）：父类未覆盖 Throwable，保留捕获语义并发布事件。
     */
    @ExceptionHandler(Throwable.class)
    public ServerResponse handleError(Throwable e) {
        log.error("[LogExceptionHandler:Throwable] Throwable 服务器异常", e);
        sysLogUtil.publishLogErrorEvent(e);
        return ServerResponse.errorResponseCode(ResponseCode.SYSTEM_INTERNAL_ERROR);
    }

}
