package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 角色查询 DTO
 *
 * @author jycloud
 * @since 2026-08-18
 */
@Data
@Schema(description = "角色查询请求")
public class RoleQueryDTO extends QueryRequest {

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "内置角色 0不是 1是")
    private Integer readonly;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
