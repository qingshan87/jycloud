package com.jycloud.service.auth.service.impl;

import com.jycloud.api.user.entity.User;
import com.jycloud.service.auth.constant.GrantTypeEnum;
import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.service.AuthHelper;
import com.jycloud.service.auth.service.ITokenGranter;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.service.UserService;
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
public class AuthorizationTokenGranter implements ITokenGranter {

    public static final String GRANT_TYPE = GrantTypeEnum.AUTHORIZATION.getCode();

    private final UserService userService;
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

        String username = JWTUtil.getUsername(token);
        String userId = JWTUtil.getUserId(token);
        String tenantId = JWTUtil.getTenantId(token);
        if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(userId)) {
            throw new SysException("非法token令牌,解析失败");
        }

        // 验证redis中是否存在该token
        if (!JWTUtil.verify(token, username)) {
            throw new SysException(ResponseCode.UNAUTHORIZED);
        }

        if (StringUtils.isBlank(userId)) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        User user = userService.getUserById(Long.valueOf(userId));
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new SysException(ResponseCode.USER_IS_DISABLED);
        }

        return authHelper.buildAuthInfoWithExistingToken(user, tenantId, authorization);
    }

    @Override
    public String grantType() {
        return GRANT_TYPE;
    }
}
