package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jycloud.tool.mp.base.TenantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-27
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_role")
@Schema(name = "Role", description = "角色表")
public class Role extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;

    @Schema(description = "角色描述")
    @TableField("remark")
    private String remark;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "内置角色(0不是,1是)")
    @TableField("readonly")
    private Integer readonly;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;

}
