package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jycloud.tool.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 租户表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_tenant")
@Schema(name = "Tenant", description = "租户表")
public class Tenant extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;

    @Schema(description = "租户名称")
    @TableField("tenant_name")
    private String tenantName;

    @Schema(description = "域名地址")
    @TableField("domain")
    private String domain;

    @Schema(description = "联系人")
    @TableField("linkman")
    private String linkman;

    @Schema(description = "联系电话")
    @TableField("contact_tel")
    private String contactTel;

    @Schema(description = "联系地址")
    @TableField("address")
    private String address;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;

}
