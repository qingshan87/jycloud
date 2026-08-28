package com.jycloud.api.user.dto;

import com.jycloud.tool.common.base.QueryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典查询 DTO
 *
 * @author jycloud
 * @since 2026-08-27
 */
@Data
@Schema(description = "字典查询请求")
public class DictQueryDTO extends QueryRequest {

    @Schema(description = "字典码")
    private String code;

    @Schema(description = "字典名称")
    private String dictValue;

}
