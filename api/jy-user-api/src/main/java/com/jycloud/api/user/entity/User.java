package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.jycloud.tool.common.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author jycloud
 * @since 2026-07-23
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
@Schema(name = "User", description = "用户表")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField("unionid")
    private String unionid;

    @Schema(description = "用户名")
    @TableField(value = "username",condition = SqlCondition.LIKE_RIGHT)
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    private String password;

    @Schema(description = "密码盐值")
    @TableField("salt")
    private String salt;

    @Schema(description = "真实姓名")
    @TableField(value = "realname",condition = SqlCondition.LIKE_RIGHT)
    private String realname;

    @Schema(description = "昵称")
    @TableField("nickname")
    private String nickname;

    @Schema(description = "用户头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "联系电话")
    @TableField("mobile")
    private String mobile;

    @Schema(description = "身份证号码")
    @TableField("id_card_no")
    private String idCardNo;

    @Schema(description = "0保密1男2女")
    @TableField("ssex")
    private Integer ssex;

    @Schema(description = "生日")
    @TableField("birthday")
    private LocalDate birthday;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "部门ID")
    @TableField("dept_id")
    private Long deptId;

    @Schema(description = "最后登录ip地址")
    @TableField("last_login_ip")
    private String lastLoginIp;

    @Schema(description = "状态 0禁用1启用")
    @TableField("status")
    private Integer status;

}
