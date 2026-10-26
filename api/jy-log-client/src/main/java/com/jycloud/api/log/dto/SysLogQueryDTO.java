package com.jycloud.api.log.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 系统操作日志查询 DTO
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Data
@Schema(description = "系统操作日志查询请求")
public class SysLogQueryDTO extends QueryRequest {

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

    @Schema(description = "操作人ID")
    private Long createUid;

    @Schema(description = "创建时间范围开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(description = "创建时间范围结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
