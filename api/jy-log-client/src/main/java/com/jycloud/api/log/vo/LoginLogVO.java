package com.jycloud.api.log.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志 VO
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Data
@Schema(description = "登录日志")
public class LoginLogVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "登录用户ID")
    private Long userId;

    @Schema(description = "登录账号")
    private String username;

    @Schema(description = "登录结果 0失败 1成功")
    private Integer type;

    @Schema(description = "登录方式")
    private String grantType;

    @Schema(description = "登录描述")
    private String description;

    @Schema(description = "登录时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;

    @Schema(description = "User-Agent")
    private String ua;

    @Schema(description = "浏览器名称")
    private String browser;

    @Schema(description = "浏览器版本")
    private String browserVersion;

    @Schema(description = "操作系统")
    private String operatingSystem;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "登录IP")
    private String ip;
}
