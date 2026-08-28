package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * 角色信息 VO
 *
 * @author jycloud
 * @since 2026-08-18
 */
@Data
@Schema(description = "角色信息")
public class RoleVO {

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色描述")
    private String remark;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "内置角色 0不是 1是")
    private Integer readonly;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "绑定的菜单ID集合")
    private Set<Long> menuIds;
}
