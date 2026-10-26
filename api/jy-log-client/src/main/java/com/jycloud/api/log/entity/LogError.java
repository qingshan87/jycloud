package com.jycloud.api.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 错误日志（对应老系统 jy-user 的 t_log_error 表，客户端只写使用）
 *
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_log_error")
@Schema(description = "错误日志")
public class LogError implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "应用名称")
    private String serviceId;

    @Schema(description = "登录人账号")
    private String username;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "方法名")
    private String classPath;

    @Schema(description = "请求方法")
    private String actionMethod;

    @Schema(description = "请求地址")
    private String requestUri;

    @Schema(description = "请求类型 POST GET")
    private String httpMethod;

    @Schema(description = "请求参数")
    private String params;

    @Schema(description = "异常描述")
    private String exDetail;

    @Schema(description = "浏览器")
    private String ua;

    @Schema(description = "创建人ID")
    private Long createUid;

    @Schema(description = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

}
