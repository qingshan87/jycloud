package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设置用户部门请求
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "设置用户部门请求")
public class UserDeptDTO {

    @NotNull(message = "{required}")
    @Min(value = 1)
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "部门ID,为空表示解绑部门")
    private Long deptId;
}
