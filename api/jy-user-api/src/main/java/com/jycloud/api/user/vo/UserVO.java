package com.jycloud.api.user.vo;

import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.entity.UserDataPermission;
import com.jycloud.api.user.entity.UserDept;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 VO（不包含密码）
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Data
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "unionid")
    private String unionid;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realname;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "联系电话")
    private String mobile;

    @Schema(description = "身份证号码")
    private String idCardNo;

    @Schema(description = "0保密1男2女")
    private Integer ssex;

    @Schema(description = "生日")
    private LocalDate birthday;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "最后登录ip")
    private String lastLoginIp;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "用户在租户中的状态: 0禁用 1正常")
    private Integer userTenantStatus;

    @Schema(description = "用户角色")
    private List<Role> roles;

    @Schema(description = "用户部门")
    private UserDept userDept;

    @Schema(description = "用户权限")
    private UserDataPermission dataPermission;

}
