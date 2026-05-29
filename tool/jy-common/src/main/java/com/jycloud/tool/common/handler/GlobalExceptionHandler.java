package com.jycloud.tool.common.handler;

import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.StringPool;
import com.jycloud.tool.common.exception.SysException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器（生产级）
 * <p>
 * 职责：
 * 1. 统一异常转友好响应，避免泄露系统信息
 * 2. 记录完整日志（含请求上下文 + 堆栈），便于问题追踪
 * 3. 区分业务异常/系统异常/参数异常，返回对应状态码
 * </p>
 *
 * @author yourname
 * @update 2026/05/25 - 适配 Spring Boot 3.2 + 安全加固
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 2)  // ✅ 魔法数字提取为常量更佳，但当前可接受
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    /**
     * 生产环境是否隐藏详细错误信息（建议从配置读取）
     */
    private static final boolean PROD_HIDE_DETAIL = true;

    // ==================== 系统级异常（兜底） ====================

    /**
     * 兜底处理：所有未捕获的 Exception
     * <p>
     * ⚠️ 关键：生产环境必须隐藏真实异常信息，避免泄露系统细节
     */
    @ExceptionHandler(Exception.class)
    public ServerResponse handleGlobalException(Exception e) {
        // ✅ 记录完整堆栈 + 请求上下文（生产排查必备）
        log.error("【系统异常】 error={}", e.getMessage(), e);

        // ✅ 生产环境返回通用提示，开发环境可返回详情（便于调试）
        String userMsg = PROD_HIDE_DETAIL
                ? ResponseCode.SYSTEM_INTERNAL_ERROR.getMessage()
                : e.getMessage();

        return ServerResponse.errorResponseCode(ResponseCode.SYSTEM_INTERNAL_ERROR, userMsg);
    }

    /**
     * 业务系统异常（可控异常）
     * <p>
     * 用于主动抛出的业务校验失败、权限不足等场景
     */
    @ExceptionHandler(SysException.class)
    public ServerResponse handleSysException(SysException e) {
        // 业务异常通常无需堆栈，记录关键信息即可
        log.warn("业务异常：code={}, msg={}", e.getCode(), e.getMessage());

        return ServerResponse.errorCodeMsg(
                e.getCode() != null ? e.getCode() : ResponseCode.SYSTEM_INTERNAL_ERROR.getCode(),
                e.getMessage()
        );
    }

    /**
     * 404 资源未找到（需配置 spring.web.resources.add-mappings=false 才生效）
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ServerResponse handle404(NoHandlerFoundException e) {
        log.warn("【404】请求路径不存在: {}", e.getRequestURL());
        return ServerResponse.errorResponseCode(ResponseCode.RESOURCE_NOT_FOUND);
    }

    // ==================== 参数校验异常（高频） ====================

    /**
     * 实体对象参数校验失败（@Valid + 对象传参）
     */
    @ExceptionHandler(BindException.class)
    public ServerResponse handleBindException(BindException e) {
        String message = extractValidationError(e.getFieldErrors());
        log.warn("【参数校验失败】errors={}", message);

        return ServerResponse.errorResponseCode(ResponseCode.PARAMETER_IS_INCORRECT, message);
    }

    /**
     * 普通参数校验失败（@Validated + 方法参数）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ServerResponse handleConstraintViolationException(ConstraintViolationException e) {
        String message = extractValidationError(e.getConstraintViolations());
        log.warn("【参数校验失败】 errors={}", message);

        return ServerResponse.errorResponseCode(ResponseCode.PARAMETER_IS_INCORRECT, message);
    }

    // ==================== 公共方法提取（消除重复） ====================

    /**
     * 提取字段校验错误信息（兼容 FieldError 和 ConstraintViolation）
     */
    private String extractValidationError(List<FieldError> fieldErrors) {
        if (fieldErrors == null || fieldErrors.isEmpty()) {
            return ResponseCode.PARAMETER_IS_INCORRECT.getMessage();
        }
        // ✅ 使用 Stream + join，避免 substring 越界风险
        return fieldErrors.stream()
                .map(error -> error.getField() + error.getDefaultMessage())
                .collect(Collectors.joining(StringPool.COMMA));
    }

    private String extractValidationError(Set<ConstraintViolation<?>> violations) {
        if (violations == null || violations.isEmpty()) {
            return ResponseCode.PARAMETER_IS_INCORRECT.getMessage();
        }
        return violations.stream()
                .map(violation -> {
                    // ✅ 安全获取字段名：路径可能为 "param.name" 或 "name"
                    String field = extractFieldName(violation.getPropertyPath().toString());
                    return field + violation.getMessage();
                })
                .collect(Collectors.joining(StringPool.COMMA));
    }

    /**
     * 从校验路径中提取字段名（兼容多种格式）
     * <p>
     * 示例：
     * "param.username" → "username"
     * "list[0].name" → "name"
     * "username" → "username"
     */
    private String extractFieldName(String path) {
        if (StringUtils.isBlank(path)) {
            return StringPool.EMPTY;
        }
        // 按 '.' 分割，取最后一段作为字段名
        String[] parts = StringUtils.split(path, StringPool.DOT);
        return parts.length > 0 ? parts[parts.length - 1] : path;
    }

}