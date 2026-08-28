package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 业务系统 VO（不包含秘钥、盐等敏感字段）
 *
 * @author jycloud
 * @since 2026-08-15
 */
@Data
@Schema(description = "业务系统信息")
public class ClientVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "应用ID")
    private String clientId;

    @Schema(description = "父级应用ID")
    private String parentId;

    @Schema(description = "类型 0内部系统 1业务系统 2外部系统")
    private Integer category;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "业务系统名")
    private String appName;

    @Schema(description = "appkey")
    private String appKey;

    @Schema(description = "业务系统简介")
    private String description;

    @Schema(description = "网站地址")
    private String website;

    @Schema(description = "合法IP,逗号分割")
    private String legalIp;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
