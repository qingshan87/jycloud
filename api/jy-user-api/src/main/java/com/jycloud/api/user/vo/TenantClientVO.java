package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户绑定的应用信息 VO
 *
 * @author jycloud
 * @since 2026-08-16
 */
@Data
@Schema(description = "租户绑定的应用信息")
public class TenantClientVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "应用ID")
    private String clientId;

    @Schema(description = "绑定状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "类型 0内部系统 1业务系统 2外部系统")
    private Integer category;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "业务系统简介")
    private String description;

    @Schema(description = "网站地址")
    private String website;

    @Schema(description = "应用状态 0禁用 1启用")
    private Integer clientStatus;
}
