package com.jycloud.service.auth.service;

import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.vo.UserTenantVO;
import com.jycloud.api.user.vo.UserVO;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.assemble.UserPermissionAssembleService;
import com.jycloud.service.user.assemble.UserRoleAssembleService;
import com.jycloud.service.user.cache.TokenCache;
import com.jycloud.service.user.service.UserService;
import com.jycloud.service.user.service.UserTenantService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.LoginVal;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.common.util.IpUtil;
import com.jycloud.tool.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final UserTenantService userTenantService;
    private final TokenCache tokenCache;
    private final UserService userService;
    private final UserRoleAssembleService userRoleAssembleService;
    private final UserPermissionAssembleService userPermissionAssembleService;


    /**
     * 为用户构建完整的 AuthInfo（密码登录 / 切换租户）：生成新 token 并更新最后登录 IP
     *
     * @param user     用户实体
     * @param tenantId 指定租户ID（可为空，为空时自动解析默认租户）
     * @return AuthInfo
     */
    public AuthInfo buildAuthInfo(User user, String tenantId) {
        List<UserTenantVO> tenantDtos = buildTenantDtos(user.getUserId());
        UserTenantVO tenantDto = resolveTenant(tenantDtos, tenantId);

        String token = generateAndCacheToken(user, tenantDto.getTenantId());
        updateLastLoginIp(user);

        return assembleAuthInfo(token, user, tenantDto, tenantDtos);
    }

    /**
     * 微信授权登录：用户已登录且客户端 IP 与最后登录 IP 一致时，复用已有 token，
     * 避免将电脑端等已登录设备挤下线；否则生成新 token 并更新最后登录 IP
     *
     * @param user     用户实体
     * @param tenantId 指定租户ID（可为空，为空时自动解析默认租户）
     * @return AuthInfo
     */
    public AuthInfo buildAuthInfoWithTokenReuse(User user, String tenantId) {
        List<UserTenantVO> tenantDtos = buildTenantDtos(user.getUserId());
        UserTenantVO tenantDto = resolveTenant(tenantDtos, tenantId);

        String userId = String.valueOf(user.getUserId());
        String existingToken = tokenCache.getToken(userId);
        String clientIp = IpUtil.getClientIp();
        boolean ipMatched = StringUtils.isNotBlank(clientIp) && clientIp.equals(user.getLastLoginIp());

        String token;
        if (StringUtils.isNotBlank(existingToken) && ipMatched) {
            // 已登录且 IP 一致，复用已有 token，避免挤下线
            token = existingToken;
            log.info("微信授权登录复用已有 token, userId={}", userId);
        } else {
            token = generateAndCacheToken(user, tenantDto.getTenantId());
            updateLastLoginIp(user);
        }

        return assembleAuthInfo(token, user, tenantDto, tenantDtos);
    }

    /**
     * token 授权登录：复用现有 token，不重新生成，避免将其他端挤下线
     *
     * @param user          用户实体
     * @param tenantId      指定租户ID（可为空，为空时自动解析默认租户）
     * @param existingToken 已校验通过的现有 token（加密后）
     * @return AuthInfo
     */
    public AuthInfo buildAuthInfoWithExistingToken(User user, String tenantId, String existingToken) {
        List<UserTenantVO> tenantDtos = buildTenantDtos(user.getUserId());
        UserTenantVO tenantDto = resolveTenant(tenantDtos, tenantId);
        return assembleAuthInfo(existingToken, user, tenantDto, tenantDtos);
    }

    /**
     * 生成新 token 并写入缓存
     */
    private String generateAndCacheToken(User user, String tenantId) {
        String rawToken = JWTUtil.createToken(user.getUsername(), String.valueOf(user.getUserId()), tenantId);
        String token = JWTUtil.encryptToken(rawToken);
        tokenCache.cacheToken(token, String.valueOf(user.getUserId()));
        return token;
    }

    /**
     * 更新用户最后登录 IP
     */
    private void updateLastLoginIp(User user) {
        String ip = IpUtil.getClientIp();
        if (StringUtils.isNotBlank(ip)) {
            userService.updateLastLoginIp(user.getUserId(), ip);
        }
    }

    /**
     * 组装 AuthInfo
     */
    private AuthInfo assembleAuthInfo(String token, User user, UserTenantVO tenantDto, List<UserTenantVO> tenantDtos) {
        UserVO userVO = DtoEntityUtil.trans(user, UserVO.class);

        AuthInfo authInfo = new AuthInfo();
        authInfo.setToken(token);
        authInfo.setExipreTime(LocalDateTime.now().plusSeconds(86400).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        authInfo.setUser(userVO);
        authInfo.setTenantId(tenantDto.getTenantId());
        authInfo.setTenantDto(tenantDto);
        authInfo.setTenantDtos(tenantDtos);

        Long userId = user.getUserId();
        String tenantId = tenantDto.getTenantId();
        // 登录场景无 Token 时多租户插件会回退默认租户，导致 t_role 等表查不到数据；
        // 临时写入目标租户上下文，确保角色/权限/菜单按目标租户组装，查询后恢复原上下文
        LoginVal original = UserContext.get();
        UserContext.set(buildTenantContext(original, tenantId));
        try {
            // 用户在该租户下的角色标识集合（role_name，缓存优先）
            authInfo.setRoles(userRoleAssembleService.listUserRoles(userId, tenantId).stream()
                    .map(Role::getRoleName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.toSet()));
            // 用户在该租户下的权限标识集合（perms，缓存优先）
            authInfo.setPermissions(userPermissionAssembleService.listPermissions(userId, tenantId));
        } finally {
            if (original == null) {
                UserContext.clear();
            } else {
                UserContext.set(original);
            }
        }

        return authInfo;
    }

    /**
     * 基于原上下文拷贝一份，仅替换租户 ID（不修改原对象）
     */
    private LoginVal buildTenantContext(LoginVal original, String tenantId) {
        LoginVal context = new LoginVal();
        if (original != null) {
            context.setUserId(original.getUserId());
            context.setUserName(original.getUserName());
            context.setClientId(original.getClientId());
            context.setProjectId(original.getProjectId());
            context.setToken(original.getToken());
            context.setTraceId(original.getTraceId());
        }
        context.setTenantId(tenantId);
        return context;
    }

    /**
     * 解析租户：
     * 1. 指定租户登录：校验用户是否属于该租户，租户禁用则抛异常
     * 2. 未指定租户：优先默认租户，默认禁用则选第一个可用租户，全部禁用则抛异常
     *
     * @param tenantDtos 用户关联的租户列表
     * @param tenantId   指定租户ID（可为空）
     * @return 解析后的租户 VO
     */
    private UserTenantVO resolveTenant(List<UserTenantVO> tenantDtos, String tenantId) {
        if (StringUtils.isNotBlank(tenantId)) {
            // 指定租户登录
            UserTenantVO target = tenantDtos.stream()
                    .filter(t -> tenantId.equals(t.getTenantId()))
                    .findFirst()
                    .orElseThrow(() -> new SysException(ResponseCode.FORBIDDEN));
            if (target.getStatus() == null || target.getStatus() != 1) {
                throw new SysException("该租户已被禁用");
            }
            return target;
        }

        // 未指定租户，优先默认租户
        UserTenantVO defaultTenant = tenantDtos.stream()
                .filter(t -> t.getIfDefault() != null && t.getIfDefault() == 1)
                .findFirst()
                .orElse(null);
        if (defaultTenant != null && defaultTenant.getStatus() != null && defaultTenant.getStatus() == 1) {
            return defaultTenant;
        }

        // 默认租户不可用，找第一个未禁用的租户
        UserTenantVO firstAvailable = tenantDtos.stream()
                .filter(t -> t.getStatus() != null && t.getStatus() == 1)
                .findFirst()
                .orElse(null);
        if (firstAvailable != null) {
            return firstAvailable;
        }

        throw new SysException("所有租户均已被禁用");
    }

    /**
     * 构建用户的所有租户信息列表
     */
    private List<UserTenantVO> buildTenantDtos(Long userId) {
        return userTenantService.listUserTenantVOs(userId);
    }
}
