package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 菜单/按钮创建更新请求
 *
 * @author jycloud
 * @since 2026-08-17
 */
@Data
@Schema(description = "菜单/按钮创建更新请求")
public class MenuDTO {

    @NotNull(message = "{required}", groups = {ValidationGroups.Update.class})
    @Schema(description = "菜单/按钮ID")
    private Long menuId;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(max = 12, message = "{noMoreThan}")
    @Schema(description = "应用ID")
    private String clientId;

    @Schema(description = "上级菜单ID,菜单顶级节点传0")
    private Long parentId;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(max = 50, message = "{noMoreThan}")
    @Schema(description = "菜单/按钮名称Title")
    private String menuTitle;

    @Size(max = 50, message = "{noMoreThan}")
    @Schema(description = "菜单/按钮名称")
    private String menuName;

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "对应路由path,菜单类型必填")
    private String path;

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "对应路由组件component")
    private String component;

    @Size(max = 80, message = "{noMoreThan}")
    @Schema(description = "权限标识,按钮类型必填")
    private String perms;

    @Size(max = 50, message = "{noMoreThan}")
    @Schema(description = "图标")
    private String icon;

    @NotNull(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Range(min = 0, max = 1)
    @Schema(description = "类型 0菜单 1按钮")
    private Integer category;

    @Range(min = 0, max = 1)
    @Schema(description = "是否隐藏;1隐藏0不隐藏")
    private Integer hide;

    @Schema(description = "排序")
    private Integer sort;

    @Range(min = 0, max = 1)
    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
