package com.jycloud.api.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.jycloud.tool.common.base.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "用户创建/更新请求")
public class UserDTO {

    @NotNull(message = "{required}", groups = {ValidationGroups.Update.class})
    // @Min(value = 1)
    @Schema(description = "用户ID")
    private Long userId;

    // @Schema(description = "用户名")
    // private String unionid;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(min = 5, max = 20, message = "{range}")
    @Schema(description = "用户名")
    private String username;

    @Size(min = 2, max = 8, message = "{range}")
    @Schema(description = "真实姓名")
    private String realname;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Size(max = 50, message = "{noMoreThan}")
    @Email(message = "{email}")
    @Schema(description = "邮箱")
    private String email;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Schema(description = "联系电话")
    private String mobile;

    @Schema(description = "身份证号码")
    private String idCardNo;

    @Schema(description = "0保密1男2女")
    private Integer ssex;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Size(max = 100, message = "{noMoreThan}")
    @Schema(description = "描述")
    private String description;

    @Size(max = 12, message = "{noMoreThan}")
    @Schema(description = "租户ID,仅setting平台按指定租户创建用户时使用")
    private String tenantId;

    // @Schema(description = "最后登录ip地址")
    // private String lastLoginIp;

    @Schema(description = "状态 0禁用1启用")
    private Integer status;
}
