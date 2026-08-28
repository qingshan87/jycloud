package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 租户绑定应用请求
 *
 * @author jycloud
 * @since 2026-08-16
 */
@Data
@Schema(description = "租户绑定应用请求")
public class TenantClientDTO {

    @NotBlank(message = "{required}")
    @Schema(description = "租户ID")
    private String tenantId;

    @NotBlank(message = "{required}")
    @Schema(description = "应用ID")
    private String clientId;

    @Range(min = 0, max = 1)
    @Schema(description = "绑定状态 0禁用 1启用")
    private Integer status;
}
