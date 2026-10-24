package com.jycloud.api.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 设置用户角色/资源请求
 *
 * @author jycloud
 * @since 2026-08
 */
@Data
@Schema(description = "设置用户角色/资源请求")
public class UserAuthorityDTO {

    @NotNull(message = "{required}")
    @Min(value = 1)
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "数据ID集合(角色ID或菜单ID),为空表示清空")
    private Set<Long> dataIds;
}
