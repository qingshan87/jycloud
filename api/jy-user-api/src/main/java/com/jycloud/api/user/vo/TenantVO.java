package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户信息 VO
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Data
@Schema(description = "租户信息")
public class TenantVO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "域名地址")
    private String domain;

    @Schema(description = "联系人")
    private String linkman;

    @Schema(description = "联系电话")
    private String contactTel;

    @Schema(description = "联系地址")
    private String address;

    @Schema(description = "租户状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
