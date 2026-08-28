package com.jycloud.tool.permission.annotation;

import java.lang.annotation.*;

/**
 * 接口权限校验注解
 * <p>
 * 标注在 Controller 方法上，AOP 切面自动拦截并校验用户是否拥有指定权限标识。
 * 权限标识来源于 {@code t_menu.perms} 字段，格式为 {@code module:resource:action}。
 * </p>
 *
 * <pre>{@code
 * @RequirePermission("system:user:add")
 * @PostMapping
 * public ServerResponse<UserVO> create(...) { ... }
 * }</pre>
 *
 * @author jycloud
 * @since 2026-08
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识，如 "system:user:add"
     */
    String value();
}
