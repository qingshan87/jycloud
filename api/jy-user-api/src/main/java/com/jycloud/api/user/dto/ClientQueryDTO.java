package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务系统查询 DTO
 *
 * @author jycloud
 * @since 2026-08-15
 */
@Data
@Schema(description = "业务系统查询请求")
public class ClientQueryDTO extends QueryRequest {

    @Schema(description = "应用ID")
    private String clientId;

    @Schema(description = "类型 0内部系统 1业务系统 2外部系统")
    private Integer category;

    @Schema(description = "业务系统名")
    private String appName;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
