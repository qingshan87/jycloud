package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jycloud.tool.common.base.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 租户授权的应用表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_tenant_client")
@Schema(name = "TenantClient", description = "租户授权的应用表")
public class TenantClient extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "应用ID")
    @TableField("client_id")
    private String clientId;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;
}
