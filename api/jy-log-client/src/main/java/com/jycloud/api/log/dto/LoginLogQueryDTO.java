package com.jycloud.api.log.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 登录日志查询 DTO
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Data
@Schema(description = "登录日志查询请求")
public class LoginLogQueryDTO extends QueryRequest {

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

    @Schema(description = "登录IP")
    private String ip;

    @Schema(description = "浏览器名称")
    private String browser;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "登录时间范围开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(description = "登录时间范围结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
