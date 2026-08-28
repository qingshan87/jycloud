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
 * 部门表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_dept")
@Schema(name = "Dept", description = "部门表")
public class Dept extends TenantEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "部门ID")
    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    @Schema(description = "上级部门ID")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "部门名称")
    @TableField("dept_name")
    private String deptName;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "状态 0锁定 1有效")
    @TableField("status")
    private Integer status;
}
