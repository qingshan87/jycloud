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
 * 用户数据权限表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@TableName("t_user_data_permission")
@Schema(name = "UserDataPermission", description = "用户数据权限表")
public class UserDataPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "数据权限范围： all（全部）、dept（部门）、myself（自己）")
    @TableField("data_scope")
    private String dataScope;

    @Schema(description = "部门ID")
    @TableField("dept_id")
    private String deptId;

    @Schema(description = "租户ID")
    @TableField("tenant_id")
    private String tenantId;
}
