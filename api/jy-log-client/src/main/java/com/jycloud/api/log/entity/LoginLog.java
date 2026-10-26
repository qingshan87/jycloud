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
 * 登录日志（对应老系统 jy-user 的 t_login_log 表，客户端只写使用）
 *
 * @author admin
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_login_log")
@Schema(description = "登录日志")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "登录人ID")
    private Long userId;

    @Schema(description = "登录人账号")
    private String username;

    @Schema(description = "登录成功失败,0失败,1成功")
    private Integer type;

    @Schema(description = "登录类型")
    private String grantType;

    @Schema(description = "登录描述")
    private String description;

    @Schema(description = "登录时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime loginTime;

    @Schema(description = "浏览器请求头")
    private String ua;

    @Schema(description = "浏览器名称")
    private String browser;

    @Schema(description = "浏览器版本")
    private String browserVersion;

    @Schema(description = "操作系统")
    private String operatingSystem;

    @Schema(description = "登录地点")
    private String location;

    @Schema(description = "登录IP")
    private String ip;

}
