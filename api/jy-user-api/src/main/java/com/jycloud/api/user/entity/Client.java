package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jycloud.tool.mp.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 业务系统表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_client")
@Schema(name = "Client", description = "业务系统表")
public class Client extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "应用ID")
    @TableField("client_id")
    private String clientId;

    @Schema(description = "父级应用ID")
    @TableField("parent_id")
    private String parentId;

    @Schema(description = "类型,0内部系统,1业务系统, 2外部系统")
    @TableField("category")
    private Integer category;

    @TableField("icon")
    private String icon;

    @Schema(description = "业务系统名")
    @TableField("app_name")
    private String appName;

    @Schema(description = "appkey")
    @TableField("app_key")
    private String appKey;

    @Schema(description = "秘钥")
    @TableField("app_secret")
    private String appSecret;

    @Schema(description = "盐")
    @TableField("salt")
    private String salt;

    @Schema(description = "业务系统简介")
    @TableField("description")
    private String description;

    @Schema(description = "网站地址")
    @TableField("website")
    private String website;

    @Schema(description = "合法IP,逗号分割")
    @TableField("legal_ip")
    private String legalIp;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;
}
