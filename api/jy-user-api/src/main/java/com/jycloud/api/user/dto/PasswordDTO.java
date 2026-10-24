package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改密码请求
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "修改密码请求")
public class PasswordDTO {

    @NotBlank(message = "{required}")
    @Schema(description = "原密码")
    private String oldPassword;

    @NotBlank(message = "{required}")
    @Size(min = 6, max = 20, message = "{range}")
    @Schema(description = "新密码,6-20位")
    private String newPassword;
}
