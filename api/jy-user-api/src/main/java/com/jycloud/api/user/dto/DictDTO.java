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
 * 字典创建/更新请求
 *
 * @author jycloud
 * @since 2026-08-27
 */
@Data
@Schema(description = "字典创建/更新请求")
public class DictDTO {

    @NotNull(message = "{required}", groups = {ValidationGroups.Update.class})
    @Min(value = 1)
    @Schema(description = "主键")
    private Long dictId;

    @Schema(description = "父主键,0表示顶级字典类型")
    private Long parentId;

    @Size(max = 30, message = "{noMoreThan}")
    @Schema(description = "字典码(子项自动继承父类型,无需传)")
    private String code;

    @Size(max = 50, message = "{noMoreThan}")
    @Schema(description = "字典值(仅子项填写)")
    private String dictKey;

    @NotBlank(message = "{required}", groups = {ValidationGroups.Insert.class})
    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "字典名称(类型名称/字典项显示名)")
    private String dictValue;

    @Schema(description = "排序")
    private Integer sort;

    @Size(max = 255, message = "{noMoreThan}")
    @Schema(description = "字典备注")
    private String remark;

    @Range(min = 0, max = 1)
    @Schema(description = "状态 0禁用 1启用")
    private Integer status;
}
