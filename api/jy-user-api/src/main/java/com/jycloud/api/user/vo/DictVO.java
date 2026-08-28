package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 字典信息 VO
 *
 * @author jycloud
 * @since 2026-08-27
 */
@Data
@Schema(description = "字典信息")
public class DictVO {

    @Schema(description = "主键")
    private Long dictId;

    @Schema(description = "父主键,0表示顶级字典类型")
    private Long parentId;

    @Schema(description = "字典码")
    private String code;

    @Schema(description = "字典值")
    private String dictKey;

    @Schema(description = "字典名称")
    private String dictValue;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "字典备注")
    private String remark;

    @Schema(description = "状态 0禁用 1启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "子节点(字典项)")
    private List<DictVO> children;
}
