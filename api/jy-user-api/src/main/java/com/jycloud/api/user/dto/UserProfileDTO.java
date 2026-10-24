package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑个人信息请求
 *
 * @author jycloud
 * @since 2026-09-02
 */
@Data
@Schema(description = "编辑个人信息请求")
public class UserProfileDTO {

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "用户头像")
    private String avatar;

    @Size(min = 2, max = 8, message = "{range}")
    @Schema(description = "真实姓名")
    private String realname;

    @Size(max = 50, message = "{noMoreThan}")
    @Email(message = "{email}")
    @Schema(description = "邮箱")
    private String email;

    @Pattern(regexp = "^((13[0-9])|(14[5,7,9])|(15[^4])|(16[^4])|(18[0-9])|(19[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$", message = "{mobile}")
    @Schema(description = "联系电话")
    private String mobile;

    @Min(value = 0, message = "{invalid}")
    @Max(value = 2, message = "{invalid}")
    @Schema(description = "0保密1男2女")
    private Integer ssex;

    @Size(max = 100, message = "{noMoreThan}")
    @Schema(description = "描述")
    private String description;
}
