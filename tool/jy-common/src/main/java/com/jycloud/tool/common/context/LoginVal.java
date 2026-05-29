package com.jycloud.tool.common.context;

import lombok.Data;

/**
 * 登录后的Token,用户信息
 */
@Data
public class LoginVal {

    /**
     * 用戶ID
     */
    private String userId;
    /**
     * 用户账号
     */
    private String userName;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 应用ID
     */
    private String clientId;
    /**
     * 项目ID
     */
    private String projectId;
    /**
     * 登录token
     */
    private String token;

}
