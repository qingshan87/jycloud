package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 设置角色菜单请求
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "设置角色菜单请求")
public class RoleMenuDTO {

    @NotNull(message = "{required}")
    @Min(value = 1)
    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "菜单ID集合,为空表示清空角色菜单")
    private Set<Long> menuIds;
}
