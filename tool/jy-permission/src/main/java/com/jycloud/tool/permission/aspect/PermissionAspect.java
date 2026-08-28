package com.jycloud.tool.permission.aspect;

import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.permission.annotation.RequirePermission;
import com.jycloud.tool.permission.spi.PermissionProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.util.StringUtils;

import java.util.Set;

/**
 * 权限校验 AOP 切面
 * <p>
 * 拦截标注了 {@link RequirePermission} 的 Controller 方法，
 * 通过 {@link PermissionProvider} 查询用户权限集合，判断是否包含所需权限标识。
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
public class PermissionAspect {

    private final PermissionProvider permissionProvider;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        String userId = UserContext.getUserId();
        String tenantId = UserContext.getTenantId();

        // 防御：ThreadLocal 中无用户信息（理论上不会发生，但安全第一）
        if (!StringUtils.hasText(userId)) {
            log.error("【权限校验】ThreadLocal 中无用户信息，拒绝访问");
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        String requiredPerm = requirePermission.value();
        Set<String> userPermissions = permissionProvider.getPermissions(userId, tenantId);

        if (userPermissions != null && userPermissions.contains(requiredPerm)) {
            return joinPoint.proceed();
        }

        log.warn("【权限校验】用户无权限访问, userId={}, tenantId={}, requiredPerm={}",
                userId, tenantId, requiredPerm);
        throw new SysException(ResponseCode.FORBIDDEN);
    }
}
