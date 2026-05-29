package com.jycloud.tool.common.base;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 创建人
     */
    @Schema(description = "创建人ID")
    @TableField(value = "create_uid", fill = FieldFill.INSERT)
    private Long createUid;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @Schema(description = "更新人ID")
    @TableField(value = "update_uid", fill = FieldFill.INSERT_UPDATE)
    private Long updateUid;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 状态[0:未删除,1:删除]
     */
    @Schema(description = "删除标记 0有效,1删除")
    @TableLogic
    @TableField(value = "delete_flag")
    private Byte deleteFlag;


    @Schema(description = "创建人用户名,默认用 realname")
    @TableField(exist = false)
    private String createUserName;

    @Schema(description = "更新人用户名,默认用 realname")
    @TableField(exist = false)
    private String updateUserName;

    public static final String CREATE_UID = "create_uid";

    public static final String CREATE_TIME = "create_time";

    public static final String UPDATE_UID = "update_uid";

    public static final String UPDATE_TIME = "update_time";

    public static final String DELETE_FLAG = "delete_flag";

}
