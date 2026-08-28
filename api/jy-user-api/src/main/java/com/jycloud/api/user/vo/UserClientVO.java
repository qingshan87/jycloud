package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 我的应用信息 VO（不包含 appKey、legalIp 等敏感/管理字段）
 *
 * @author jycloud
 * @since 2026-08-28
 */
@Data
@Schema(description = "我的应用信息")
public class UserClientVO {

    @Schema(description = "应用ID")
    private String clientId;

    @Schema(description = "类型 0内部系统 1业务系统 2外部系统")
    private Integer category;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "业务系统名")
    private String appName;

    @Schema(description = "业务系统简介")
    private String description;

    @Schema(description = "网站地址")
    private String website;

    @Schema(description = "应用状态 0禁用 1启用")
    private Integer status;
}
