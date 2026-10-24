package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设置用户数据权限请求
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "设置用户数据权限请求")
public class UserDataPermissionDTO {

    @NotNull(message = "{required}")
    @Min(value = 1)
    @Schema(description = "用户ID")
    private Long userId;

    @NotBlank(message = "{required}")
    @Schema(description = "数据权限范围: all(全部)、dept(部门)、myself(自己)")
    private String dataScope;
}
