package com.jycloud.api.log.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * API 调用日志 VO
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Data
@Schema(description = "API调用日志")
public class SysApiLogVO {

    @Schema(description = "主键")
    private Long id;

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

    @Schema(description = "响应状态码")
    private Integer responseCode;

    @Schema(description = "响应描述")
    private String responseMsg;

    @Schema(description = "响应数据")
    private String responseData;

    @Schema(description = "异常详情")
    private String exDetail;

    @Schema(description = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;

    @Schema(description = "耗时（毫秒）")
    private Long costTime;

    @Schema(description = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "应用名称")
    private String clientName;
}
