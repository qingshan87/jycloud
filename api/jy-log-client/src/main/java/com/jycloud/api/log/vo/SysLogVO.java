package com.jycloud.api.log.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统操作日志 VO
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Data
@Schema(description = "系统操作日志")
public class SysLogVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "来源应用")
    private String serviceId;

    @Schema(description = "操作结果 0失败 1成功")
    private Integer type;

    @Schema(description = "操作账号")
    private String username;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "目标类")
    private String classPath;

    @Schema(description = "目标方法")
    private String actionMethod;

    @Schema(description = "请求地址")
    private String requestUri;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "返回结果")
    private String result;

    @Schema(description = "异常详情")
    private String exDetail;

    @Schema(description = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;

    @Schema(description = "耗时（毫秒）")
    private Long consumingTime;

    @Schema(description = "User-Agent")
    private String ua;

    @Schema(description = "操作人ID")
    private Long createUid;

    @Schema(description = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
