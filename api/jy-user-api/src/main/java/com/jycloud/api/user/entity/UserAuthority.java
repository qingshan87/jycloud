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
 * 用户权限关联表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@TableName("t_user_authority")
@Schema(name = "UserAuthority", description = "用户权限关联表")
public class UserAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "类型 0角色(role_id),1资源(menu_id)")
    @TableField("category")
    private Integer category;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "数据id")
    @TableField("data_id")
    private Long dataId;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;
}
