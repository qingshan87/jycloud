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
 * Api请求日志（对应老系统 jy-user 的 t_api_log 表，客户端只写使用）
 *
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_api_log")
@Schema(description = "Api请求日志")
public class SysApiLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "请求标识")
    private String requestId;

    @Schema(description = "发起Api请求的应用ID,数据来源平台")
    private String clientId;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "提供API的服务名称")
    private String serviceName;

    @Schema(description = "日志类型 0失败,1成功")
    private Integer type;

    @Schema(description = "操作IP")
    private String ip;

    @Schema(description = "api编号")
    private String actionId;

    @Schema(description = "api描述")
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

    @Schema(description = "响应状态")
    private Integer responseCode;

    @Schema(description = "响应描述")
    private String responseMsg;

    @Schema(description = "响应返回结果")
    private String responseData;

    @Schema(description = "异常结果")
    private String exDetail;

    @Schema(description = "开始时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;

    @Schema(description = "完成时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime finishTime;

    @Schema(description = "消耗时间, 毫秒")
    private Long costTime;

    @Schema(description = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Schema(description = "应用名称")
    private String clientName;
}
