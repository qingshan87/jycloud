package com.jycloud.service.auth.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.jycloud.api.user.entity.User;
import com.jycloud.service.auth.config.WxMaConfiguration;
import com.jycloud.service.auth.constant.GrantTypeEnum;
import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.service.AuthHelper;
import com.jycloud.service.auth.service.ITokenGranter;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.service.UserService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WechatUnionidTokenGranter implements ITokenGranter {

    public static final String GRANT_TYPE = GrantTypeEnum.WECHAT_UNIONID.getCode();

    private final UserService userService;
    private final AuthHelper authHelper;
    private final WxMaConfiguration wxMaConfiguration;

    @Override
    public AuthInfo grant(AuthDTO authDTO) throws Exception {
        String appId = authDTO.getAppId();
        String jsCode = authDTO.getJsCode();
        String tenantId = authDTO.getTenantId();

        if (StringUtils.isAnyBlank(appId, jsCode)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        WxMaService wxMaService = wxMaConfiguration.getMaService(appId);
        if (wxMaService == null) {
            throw new SysException("未找到对应的小程序配置，appId: " + appId);
        }

        // 1. code2Session 获取 openid + unionid
        WxMaJscode2SessionResult sessionResult;
        try {
            sessionResult = wxMaService.getUserService().getSessionInfo(jsCode);
        } catch (Exception e) {
            log.error("微信 code2Session 失败, appId={}, jsCode={}", appId, jsCode, e);
            throw new SysException("微信登录凭证校验失败");
        }
        if (sessionResult == null || StringUtils.isBlank(sessionResult.getUnionid())) {
            throw new SysException("未能获取到 unionid，请确认已开通微信开放平台并绑定小程序");
        }

        String unionid = sessionResult.getUnionid();
        log.info("微信 unionid 登录: unionid={}, openid={}", unionid, sessionResult.getOpenid());

        // 2. 查用户
        User user = userService.getUserByUnionid(unionid);
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new SysException(ResponseCode.USER_IS_DISABLED);
        }

        return authHelper.buildAuthInfoWithTokenReuse(user, tenantId);
    }

    @Override
    public String grantType() {
        return GRANT_TYPE;
    }
}
