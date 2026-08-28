package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.ValidationGroups;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Set;

@Data
public class RoleDTO {

    @NotNull(message = "{required}",groups = {ValidationGroups.Update.class})
    @Min(value = 1)
    @Schema(description = "角色ID")
    private Long roleId;

    @NotBlank(message = "{required}",groups = {ValidationGroups.Insert.class})
    @Schema(description = "角色名称")
    private String roleName;

    @Size(max = 100, message = "{noMoreThan}")
    @Schema(description = "角色描述")
    private String remark;

    @Schema(description = "排序")
    private Integer sort;

    @Range(min = 0,max = 1)
    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "菜单id")
    private Set<Long> menuIds;

    @Schema(description = "租户ID,仅setting平台使用")
    private String tenantId;
}
