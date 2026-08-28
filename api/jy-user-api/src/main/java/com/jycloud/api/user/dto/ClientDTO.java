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
 * 业务系统创建/更新请求
 *
 * @author jycloud
 * @since 2026-08-15
 */
@Data
@Schema(description = "业务系统创建/更新请求")
public class ClientDTO {

    @NotNull(message = "{required}", groups = {ValidationGroups.Update.class})
    @Min(value = 1)
    @Schema(description = "主键")
    private Long id;

    @Range(min = 0, max = 2)
    @Schema(description = "类型 0内部系统 1业务系统 2外部系统")
    private Integer category;

    @Schema(description = "图标")
    private String icon;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(max = 50, message = "{noMoreThan}")
    @Schema(description = "业务系统名")
    private String appName;

    @Size(max = 200, message = "{noMoreThan}")
    @Schema(description = "业务系统简介")
    private String description;

    @Size(max = 100, message = "{noMoreThan}")
    @Schema(description = "网站地址")
    private String website;

    @Schema(description = "合法IP,逗号分割")
    private String legalIp;

    @Range(min = 0, max = 1)
    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
