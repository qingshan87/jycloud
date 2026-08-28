package com.jycloud.service.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.entity.UserTenant;
import com.jycloud.service.auth.constant.GrantTypeEnum;
import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.service.AuthHelper;
import com.jycloud.service.auth.service.ITokenGranter;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.service.UserService;
import com.jycloud.service.user.service.UserTenantService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SwitchTenantTokenGranter implements ITokenGranter {

    public static final String GRANT_TYPE = GrantTypeEnum.SWITCH_TENANT.getCode();

    private final UserService userService;
    private final UserTenantService userTenantService;
    private final AuthHelper authHelper;

    @Override
    public AuthInfo grant(AuthDTO authDTO) throws Exception {
        String authorization = JWTUtil.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        String token = JWTUtil.decryptToken(authorization);
        if (StringUtils.isBlank(token)) {
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        String userId = JWTUtil.getUserId(token);
        if (StringUtils.isBlank(userId)) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        String targetTenantId = authDTO.getTenantId();
        if (StringUtils.isBlank(targetTenantId)) {
            throw new SysException("目标租户ID不能为空");
        }

        UserTenant userTenant = userTenantService.getOne(
                new LambdaQueryWrapper<UserTenant>()
                        .eq(UserTenant::getUserId, Long.valueOf(userId))
                        .eq(UserTenant::getTenantId, targetTenantId)
                        .eq(UserTenant::getStatus, 1)
        );
        if (userTenant == null) {
            throw new SysException(ResponseCode.FORBIDDEN);
        }

        User user = userService.getUserById(Long.valueOf(userId));
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new SysException(ResponseCode.USER_IS_DISABLED);
        }

        return authHelper.buildAuthInfo(user, targetTenantId);
    }

    @Override
    public String grantType() {
        return GRANT_TYPE;
    }
}
