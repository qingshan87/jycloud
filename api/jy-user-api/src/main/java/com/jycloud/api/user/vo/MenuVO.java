package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单/按钮信息 VO
 *
 * @author jycloud
 * @since 2026-08-17
 */
@Data
@Schema(description = "菜单/按钮信息")
public class MenuVO {

    @Schema(description = "菜单/按钮ID")
    private Long menuId;

    @Schema(description = "应用ID")
    private String clientId;

    @Schema(description = "上级菜单ID")
    private Long parentId;

    @Schema(description = "菜单/按钮名称Title")
    private String menuTitle;

    @Schema(description = "菜单/按钮名称")
    private String menuName;

    @Schema(description = "对应路由path")
    private String path;

    @Schema(description = "对应路由组件component")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "类型 0菜单 1按钮")
    private Integer category;

    @Schema(description = "是否隐藏;1隐藏0不隐藏")
    private Integer hide;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "子节点")
    private List<MenuVO> children;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
