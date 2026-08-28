package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@TableName("t_region")
@Schema(name = "Region", description = "")
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "邮编")
    @TableField("code_id")
    private Integer codeId;

    @Schema(description = "标志")
    @TableField("name")
    private String name;

    @Schema(description = "城市全称")
    @TableField("fullname")
    private String fullname;

    @Schema(description = "标题")
    @TableField("title")
    private String title;

    @Schema(description = "上级分类ID")
    @TableField("pid")
    private Integer pid;

    @Schema(description = "层级路径")
    @TableField("pid_path")
    private String pidPath;

    @Schema(description = "邮编父级")
    @TableField("code_pid")
    private Integer codePid;

    @Schema(description = "1省2市3区")
    @TableField("level")
    private Integer level;

    @Schema(description = "排序（同级有效）")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "数据状态(1默认)")
    @TableField("status")
    private Integer status;

    @Schema(description = "备注")
    @TableField("note")
    private String note;

    @Schema(description = "纬度")
    @TableField("lat")
    private BigDecimal lat;

    @Schema(description = "经度")
    @TableField("lon")
    private BigDecimal lon;
}
