package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 用户租户表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@TableName("t_user_tenant")
@Schema(name = "UserTenant", description = "用户租户表")
public class UserTenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "是否是默认的租户: 0不是,1是")
    @TableField("if_default")
    private Integer ifDefault;

    @Schema(description = "是否是C端用户: 0不是,1是")
    @TableField("consumer")
    private Integer consumer;

    @Schema(description = "是否是B端用户: 0不是,1是")
    @TableField("manager")
    private Integer manager;

    @Schema(description = "当前租户下的状态:0禁用 1正常")
    @TableField("status")
    private Integer status;

}
