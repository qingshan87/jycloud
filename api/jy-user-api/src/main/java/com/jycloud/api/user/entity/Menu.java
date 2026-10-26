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
 * 菜单表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_menu")
@Schema(name = "Menu", description = "菜单表")
public class Menu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "菜单/按钮ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @Schema(description = "应用ID")
    @TableField("client_id")
    private String clientId;

    @Schema(description = "上级菜单ID")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "菜单/按钮名称Title")
    @TableField("menu_title")
    private String menuTitle;

    @Schema(description = "菜单/按钮名称")
    @TableField("menu_name")
    private String menuName;

    @Schema(description = "对应路由path")
    @TableField("path")
    private String path;

    @Schema(description = "对应路由组件component")
    @TableField("component")
    private String component;

    @Schema(description = "权限标识")
    @TableField("perms")
    private String perms;

    @Schema(description = "图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "类型 0菜单 1按钮")
    @TableField("category")
    private Integer category;

    @Schema(description = "是否隐藏;1隐藏0不隐藏")
    @TableField("hide")
    private Integer hide;

    @Schema(description = "排序")
    @TableField("sort")
    private Integer sort;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;
}
