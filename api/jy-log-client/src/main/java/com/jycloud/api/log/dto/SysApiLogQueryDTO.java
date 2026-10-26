package com.jycloud.api.log.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * API 调用日志查询 DTO
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Data
@Schema(description = "API调用日志查询请求")
public class SysApiLogQueryDTO extends QueryRequest {

    @Schema(description = "链路请求标识")
    private String requestId;

    @Schema(description = "发起请求的应用ID")
    private String clientId;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "提供API的服务名称")
    private String serviceName;

    @Schema(description = "调用结果 0失败 1成功")
    private Integer type;

    @Schema(description = "调用IP")
    private String ip;

    @Schema(description = "API编号")
    private String actionId;

    @Schema(description = "API描述")
    private String description;

    @Schema(description = "请求地址")
    private String requestUri;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "响应状态码")
    private Integer responseCode;

    @Schema(description = "应用名称")
    private String clientName;

    @Schema(description = "创建时间范围开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(description = "创建时间范围结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
