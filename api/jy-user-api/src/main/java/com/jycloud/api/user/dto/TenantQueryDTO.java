package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 租户查询 DTO
 *
 * @author jycloud
 * @since 2026-08-16
 */
@Data
@Schema(description = "租户查询请求")
public class TenantQueryDTO extends QueryRequest {

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "联系人")
    private String linkman;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
