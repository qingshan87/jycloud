package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 邀请用户查询 DTO
 * <p>
 * 查询可邀请加入租户的系统用户时使用，仅包含账号/真实姓名/手机号码三个查询条件，
 * 不包含分页等冗余字段。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "邀请用户查询请求")
public class InviteUserQueryDTO {

    @Schema(description = "用户名（账号）")
    private String username;

    @Schema(description = "真实姓名")
    private String realname;

    @Schema(description = "手机号码")
    private String mobile;
}
