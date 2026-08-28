package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jycloud.tool.mp.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_dict")
@Schema(name = "Dict", description = "字典表")
public class Dict extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "dict_id", type = IdType.AUTO)
    private Long dictId;

    @Schema(description = "父主键")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "字典码")
    @TableField(value = "code")
    private String code;

    @Schema(description = "字典值")
    @TableField("dict_key")
    private String dictKey;

    @Schema(description = "字典名称")
    @TableField(value = "dict_value", condition = SqlCondition.LIKE)
    private String dictValue;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "字典备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;
}
