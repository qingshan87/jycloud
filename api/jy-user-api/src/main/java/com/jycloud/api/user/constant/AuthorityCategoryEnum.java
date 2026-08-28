package com.jycloud.api.user.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户权限关联类型枚举
 * <p>
 * t_user_authority.category 取值：
 * <ul>
 *   <li>0 - 角色（data_id 指向 t_role.role_id）</li>
 *   <li>1 - 资源（data_id 指向 t_menu.menu_id）</li>
 * </ul>
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Getter
public enum AuthorityCategoryEnum {

    /** 角色 */
    ROLE(0, "角色"),

    /** 资源（菜单） */
    MENU(1, "资源");

    private final Integer code;
    private final String desc;

    AuthorityCategoryEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<Integer, AuthorityCategoryEnum> CODE_TO_ENUM =
            Arrays.stream(values())
                    .collect(Collectors.toMap(AuthorityCategoryEnum::getCode, Function.identity()));

    public static AuthorityCategoryEnum of(Integer code) {
        return code == null ? null : CODE_TO_ENUM.get(code);
    }

    public static boolean isValid(Integer code) {
        return of(code) != null;
    }
}
