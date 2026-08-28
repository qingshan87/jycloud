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
import com.jycloud.tool.common.util.MD5Util;
import com.jycloud.tool.common.util.ValidatorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordTokenGranter implements ITokenGranter {

    public static final String GRANT_TYPE = GrantTypeEnum.PASSWORD.getCode();

    private final UserService userService;
    private final AuthHelper authHelper;

    @Override
    public AuthInfo grant(AuthDTO authDTO) throws Exception {
        String username = authDTO.getUsername();
        String password = authDTO.getPassword();
        String tenantId = authDTO.getTenantId();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new SysException(ResponseCode.ENTERED_IS_INCORRECT);
        }

        // 手机号走手机号查询，否则按用户名查询
        User user = ValidatorUtil.isMobile(username)
                ? userService.getUserByMobile(username)
                : userService.getUserByUsername(username);
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        String encryptedPassword = MD5Util.encryptWithSalt(password, user.getSalt());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new SysException(ResponseCode.ENTERED_IS_INCORRECT);
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new SysException(ResponseCode.USER_IS_DISABLED);
        }

        return authHelper.buildAuthInfo(user, tenantId);
    }

    @Override
    public String grantType() {
        return GRANT_TYPE;
    }
}
