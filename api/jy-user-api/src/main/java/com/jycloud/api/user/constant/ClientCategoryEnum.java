package com.jycloud.api.user.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ClientCategoryEnum {

    //类型,0内部系统,1业务系统, 2外部系统
    INNER(0, "内部系统"),
    BUSINESS(1, "业务系统"),
    OUTER(2, "外部系统");

    private final int code;
    private final String desc;

    ClientCategoryEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<Integer, ClientCategoryEnum> CODE_TO_ENUM =
            Arrays.stream(values())
                    .collect(Collectors.toMap(ClientCategoryEnum::getCode, Function.identity()));

    public static ClientCategoryEnum of(Integer code) {
        return code == null ? null : CODE_TO_ENUM.get(code);
    }

    public static boolean isValid(Integer code) {
        return of(code) != null;
    }

    public static boolean isInner(Integer code) {
        return INNER.code == code;
    }
    public static boolean isBusiness(Integer code) {
        return BUSINESS.code == code;
    }
    public static boolean isOuter(Integer code) {
        return OUTER.code == code;
    }
}
