package com.jycloud.tool.permission.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色校验注解
 * <p>
 * 标注在 Controller 方法上，AOP 切面自动拦截并校验用户是否拥有指定角色。
 * 角色标识取自 {@code t_role.role_name}，内置角色如 {@code super_admin}、{@code tenant_admin}。
 * </p>
 *
 * <pre>{@code
 * @RequireRole("super_admin")
 * @DeleteMapping("/user/{id}")
 * public ServerResponse<Void> delete(...) { ... }
 * }</pre>
 *
 * @author jycloud
 * @since 2026-08
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 角色标识数组，如 {"super_admin", "tenant_admin"}
     */
    String[] value();

    /**
     * 多角色匹配逻辑：
     * <ul>
     *   <li>{@link MatchMode#ANY}：拥有任意一个角色即放行（默认）</li>
     *   <li>{@link MatchMode#ALL}：必须拥有全部角色才放行</li>
     * </ul>
     */
    MatchMode mode() default MatchMode.ANY;

    /**
     * 匹配模式
     */
    enum MatchMode {
        ANY, ALL
    }
}
