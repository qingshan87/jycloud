package com.jycloud.api.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门树 VO
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "部门信息")
public class DeptVO {

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "上级部门ID")
    private Long parentId;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "状态 0锁定 1有效")
    private Integer status;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "子部门列表")
    private List<DeptVO> children;
}
