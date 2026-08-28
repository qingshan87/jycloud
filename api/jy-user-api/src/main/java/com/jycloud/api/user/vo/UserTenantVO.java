package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserTenantVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "是否是默认的租户: 0不是,1是")
    private Integer ifDefault;

    @Schema(description = "是否是C端用户: 0不是,1是")
    private Integer consumer;

    @Schema(description = "是否是B端用户: 0不是,1是")
    private Integer manager;

    @Schema(description = "用户在租户中的状态: 0禁用 1正常")
    private Integer status;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "租户状态")
    private Integer tenantStatus;

    @Schema(description = "租户详情")
    private TenantVO tenant;
}
