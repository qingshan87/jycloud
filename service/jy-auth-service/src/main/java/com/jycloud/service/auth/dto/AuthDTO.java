package com.jycloud.service.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "用户登录请求")
public class AuthDTO {
    @NotBlank(message = "{required}")
    @Schema(description = "应用ID")
    private String clientId;

    @NotBlank(message = "{required}")
    @Schema(description = "授权类型,password/authorization/switch_tenant/wechat/wechat_unionid")
    private String grantType;

    @Schema(description = "租户ID")
    private String tenantId;

    // 账号密码登录
    @Size(min = 4, max = 20, message = "{range}")
    @Schema(description = "用户名/手机号")
    private String username;
    @Schema(description = "用户密码")
    private String password;

    // 小程序授权登录
    @Schema(description = "小程序-appId, 微信小程序登录,授权手机号码/微信小程序unionid登录")
    private String appId;
    @Schema(description = "小程序-jsCode, 微信小程序登录,授权手机号码/微信小程序unionid登录")
    private String jsCode;
    @Schema(description = "小程序-加密串, 微信小程序登录,授权手机号码")
    private String encryptedData;
    @Schema(description = "小程序-偏移量, 微信小程序登录,授权手机号码")
    private String iv;

}
