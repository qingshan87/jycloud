package com.jycloud.tool.common.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Api 接口响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {


    /**
     * 操作成功
     */
    SUCCESS(200, "处理成功"),

    PARSE_MESSAGE_ERROR(301, "用户未注册"),
    INVALID_CALL_CREDENTIALS(302, "无效调用凭证"),




    /**
     * 业务异常
     */
    FAILURE(999, "处理失败"),
    SYSTEM_INTERNAL_ERROR(500, "系统内部异常"),
    PARAMETER_IS_INCORRECT(400, "参数不正确"),
    UNAUTHORIZED(401,"未授权,权限认证失败"),
    FORBIDDEN(403,"无权限操作"),
    RESOURCE_NOT_FOUND(404,""),
    REQUEST_TOO_MANY(429,"访问频繁,服务器限流"),

    /**
     * 登录/注册
     */
    USER_IS_DISABLED(1001, "用户被禁用"),
    USER_NOT_EXIST(1002, "用户未注册"),
    CHECK_NO_DATA(1005, "查无数据"),
    MOBILE_IS_EXIST(1007, "手机号已存在"),
    CODE_IS_WRONG(1008, "验证码输入有误，请重新输入"),
    ENTERED_IS_INCORRECT(1009, "您输入的用户名或密码不正确，请重新输入"),

    /**
     * 产品
     */
    PRODUCT_FAILED(2001,"产品库存不足,请联系管理员"),

    /**
     * 三方接口调用
     */
    REQUEST_FAILED(3001,"三方接口调用失败");


    /**
     * code编码
     */
    final Integer code;
    /**
     * 中文信息描述
     */
    final String message;

}
