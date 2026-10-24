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
 * 部门请求 DTO
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "部门请求")
public class DeptDTO {

    @NotNull(message = "{required}", groups = {ValidationGroups.Update.class})
    @Min(value = 1)
    @Schema(description = "部门ID")
    private Long deptId;

    @NotNull(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Min(value = 0)
    @Schema(description = "上级部门ID,0表示顶级部门")
    private Long parentId;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(max = 100, message = "{noMoreThan}")
    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "排序")
    private Integer sort;

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "备注")
    private String remark;

    @Range(min = 0, max = 1)
    @Schema(description = "状态 0锁定 1有效")
    private Integer status;
}
