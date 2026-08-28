package com.jycloud.tool.common.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
public class QueryRequest implements Serializable {

    private static final long serialVersionUID = -4869594085374385813L;

    @Schema(description = "当前页请求数量")
    private int pageSize = 10;
    @Schema(description = "当前页面码")
    private int pageNum = 1;

    // 排序字段
    @Schema(description = "排序字段")
    private String sortField;
    // 排序规则 ascend 升序 descend 降序
    @Schema(description = "排序规则,ascend 升序 descend 降序")
    private String sortOrder;


}
