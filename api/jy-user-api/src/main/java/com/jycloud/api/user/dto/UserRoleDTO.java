package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 设置用户角色请求
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "设置用户角色请求")
public class UserRoleDTO {

    @NotNull(message = "{required}")
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "租户ID,仅setting平台使用")
    private String tenantId;

    @Schema(description = "角色ID集合,为空表示清空角色")
    private Set<Long> roleIds;
}
