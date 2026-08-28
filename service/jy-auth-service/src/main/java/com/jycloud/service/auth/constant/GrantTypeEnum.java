package com.jycloud.service.auth.constant;

import lombok.Getter;

@Getter
public enum GrantTypeEnum {

    PASSWORD("password", "账号密码登录"),
    AUTHORIZATION("authorization", "token授权"),
    SWITCH_TENANT("switch_tenant", "切换租户"),
    WECHAT("wechat", "微信小程序登录,授权手机号码"),
    WECHAT_UNIONID("wechat_unionid", "微信小程序unionid登录");

    String code;
    String name;

    GrantTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
