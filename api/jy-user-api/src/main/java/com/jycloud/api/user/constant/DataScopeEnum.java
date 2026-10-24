package com.jycloud.api.user.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户数据权限范围枚举
 * <p>
 * t_user_data_permission.data_scope 取值：
 * <ul>
 *   <li>all - 全部数据</li>
 *   <li>dept - 本部门数据（部门以用户部门关联为准，dept_id 字段预留）</li>
 *   <li>myself - 仅本人数据</li>
 * </ul>
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Getter
public enum DataScopeEnum {

    /** 全部 */
    ALL("all", "全部"),

    /** 部门 */
    DEPT("dept", "部门"),

    /** 自己 */
    MYSELF("myself", "自己");

    private final String code;
    private final String desc;

    DataScopeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<String, DataScopeEnum> CODE_TO_ENUM =
            Arrays.stream(values())
                    .collect(Collectors.toMap(DataScopeEnum::getCode, Function.identity()));

    public static DataScopeEnum of(String code) {
        return code == null ? null : CODE_TO_ENUM.get(code);
    }

    public static boolean isValid(String code) {
        return of(code) != null;
    }
}
