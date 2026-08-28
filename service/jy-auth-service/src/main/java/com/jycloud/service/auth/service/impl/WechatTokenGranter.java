package com.jycloud.service.auth.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
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
public class WechatTokenGranter implements ITokenGranter {

    public static final String GRANT_TYPE = GrantTypeEnum.WECHAT.getCode();

    private final UserService userService;
    private final AuthHelper authHelper;
    private final WxMaConfiguration wxMaConfiguration;

    @Override
    public AuthInfo grant(AuthDTO authDTO) throws Exception {
        String appId = authDTO.getAppId();
        String jsCode = authDTO.getJsCode();
        String encryptedData = authDTO.getEncryptedData();
        String iv = authDTO.getIv();
        String tenantId = authDTO.getTenantId();

        if (StringUtils.isAnyBlank(appId, jsCode, encryptedData, iv)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        WxMaService wxMaService = wxMaConfiguration.getMaService(appId);
        if (wxMaService == null) {
            throw new SysException("未找到对应的小程序配置，appId: " + appId);
        }

        // 1. code2Session 获取 sessionKey
        WxMaJscode2SessionResult sessionResult;
        try {
            sessionResult = wxMaService.getUserService().getSessionInfo(jsCode);
        } catch (Exception e) {
            log.error("微信 code2Session 失败, appId={}, jsCode={}", appId, jsCode, e);
            throw new SysException("微信登录凭证校验失败");
        }
        if (sessionResult == null || StringUtils.isBlank(sessionResult.getSessionKey())) {
            throw new SysException("微信登录凭证校验返回为空");
        }

        // 2. 解密手机号
        WxMaPhoneNumberInfo phoneNumberInfo;
        try {
            phoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(sessionResult.getSessionKey(),
                    encryptedData, iv);
        } catch (Exception e) {
            log.error("微信手机号解密失败, appId={}", appId, e);
            throw new SysException("手机号解密失败");
        }
        if (phoneNumberInfo == null || StringUtils.isBlank(phoneNumberInfo.getPhoneNumber())) {
            throw new SysException("未能获取到手机号");
        }

        String phoneNumber = phoneNumberInfo.getPhoneNumber();
        log.info("微信手机号登录: phoneNumber={}", phoneNumber);

        // 3. 查用户
        User user = userService.getUserByMobile(phoneNumber);
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
