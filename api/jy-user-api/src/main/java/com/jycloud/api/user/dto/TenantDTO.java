package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 租户创建/更新请求
 *
 * @author jycloud
 * @since 2026-08-16
 */
@Data
@Schema(description = "租户创建/更新请求")
public class TenantDTO {

    @NotNull(message = "{required}", groups = {ValidationGroups.Update.class})
    @Min(value = 1)
    @Schema(description = "主键")
    private Long id;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(max = 50, message = "{noMoreThan}")
    @Schema(description = "租户名称")
    private String tenantName;

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "域名地址")
    private String domain;

    @Size(max = 20, message = "{noMoreThan}")
    @Schema(description = "联系人")
    private String linkman;

    @Size(max = 20, message = "{noMoreThan}")
    @Schema(description = "联系电话")
    private String contactTel;

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "联系地址")
    private String address;

    @Range(min = 0, max = 1)
    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
