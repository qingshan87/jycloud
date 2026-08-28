package com.jycloud.tool.permission.aspect;

import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.permission.annotation.RequireRole;
import com.jycloud.tool.permission.spi.RoleProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Set;

/**
 * 角色校验 AOP 切面
 * <p>
 * 拦截标注了 {@link RequireRole} 的 Controller 方法，
 * 通过 {@link RoleProvider} 查询用户角色集合，判断是否包含所需角色。
 * </p>
 *
 * <h3>认证前置条件</h3>
 * 本切面依赖 {@link UserContext} 中的 userId / tenantId，
 * 这些值由 Gateway 的 {@code AuthGlobalFilter} 在 JWT 认证后通过请求头透传，
 * 并由服务端的拦截器或 Filter 写入 ThreadLocal。
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class RoleAspect {

    private final RoleProvider roleProvider;

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        String userId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();

        // 防御：ThreadLocal 中无用户信息（理论上不会发生，但安全第一）
        if (!StringUtils.hasText(userId)) {
            log.error("【角色校验】ThreadLocal 中无用户信息，拒绝访问");
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        Set<String> userRoles = roleProvider.getRoles(userId, tenantId);

        if (matchRole(requireRole, userRoles)) {
            return joinPoint.proceed();
        }

        log.warn("【角色校验】用户无角色权限, userId={}, tenantId={}, requiredRoles={}",
                userId, tenantId, Arrays.toString(requireRole.value()));
        throw new SysException(ResponseCode.FORBIDDEN);
    }

    /**
     * 判断用户角色是否匹配注解要求
     */
    private boolean matchRole(RequireRole requireRole, Set<String> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return false;
        }
        if (requireRole.mode() == RequireRole.MatchMode.ALL) {
            return userRoles.containsAll(Arrays.asList(requireRole.value()));
        }
        return Arrays.stream(requireRole.value()).anyMatch(userRoles::contains);
    }
}
