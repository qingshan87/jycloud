package com.jycloud.api.log.constant;

import lombok.Getter;

/**
 * @author Lqs
 * @date 2022/7/13 11:38
 */
@Getter
public enum LogTypeEnum {

    SUCCESS(1, "成功"),
    FAIL(0, "失败");

    private Integer code;
    private String name;

    LogTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }
}
