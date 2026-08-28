package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户查询 DTO
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryDTO  extends QueryRequest {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realname;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "身份证")
    private String idCardNo;

    @Schema(description = "性别 0保密1男2女")
    private String ssex;

    @Schema(description = "状态 0禁用1启用")
    private String status;
}
