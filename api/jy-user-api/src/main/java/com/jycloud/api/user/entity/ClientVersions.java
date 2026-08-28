package com.jycloud.api.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.jycloud.tool.mp.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 应用版本管理记录
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@TableName("t_client_versions")
@Schema(name = "ClientVersions", description = "应用版本管理记录")
public class ClientVersions extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "应用ID")
    @TableField("client_id")
    private String clientId;

    @Schema(description = "版本")
    @TableField("version")
    private String version;

    @Schema(description = "描述")
    @TableField("description")
    private String description;

    @Schema(description = "发布时间")
    @TableField("release_date")
    private LocalDateTime releaseDate;

    @Schema(description = "状态")
    @TableField("status")
    private Integer status;
}
