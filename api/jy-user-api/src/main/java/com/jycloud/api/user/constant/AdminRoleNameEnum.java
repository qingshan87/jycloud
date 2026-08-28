package com.jycloud.api.user.constant;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理员角色名枚举
 * <p>
 * 系统预置的管理员角色，租户创建普通角色时不可占用这些名称：
 * super_admin 为系统级角色（全局唯一，仅存在于默认租户），
 * tenant_admin 为租户级角色（每个租户各有一个）。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Getter
public enum AdminRoleNameEnum {

    /** 超级管理员（系统级，全局唯一，仅存在于默认租户） */
    SUPER_ADMIN("super_admin", "超级管理员"),

    /** 平台管理员（暂未使用） */
    CLIENT_ADMIN("client_admin", "平台管理员"),

    /** 租户管理员（租户级，每个租户各有一个） */
    TENANT_ADMIN("tenant_admin", "租户管理员");

    /**
     * 角色名
     */
    private final String code;

    /**
     * 角色描述
     */
    private final String desc;

    AdminRoleNameEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private static final Map<String, AdminRoleNameEnum> CODE_TO_ENUM =
            Arrays.stream(values())
                    .collect(Collectors.toMap(AdminRoleNameEnum::getCode, Function.identity()));

    public static AdminRoleNameEnum of(String code) {
        return code == null ? null : CODE_TO_ENUM.get(code);
    }

    public static boolean isValid(String code) {
        return of(code) != null;
    }

    public static boolean isValidEnum(String code, AdminRoleNameEnum statusEnum) {
        return of(code) != null && of(code) == statusEnum;
    }

    /**
     * 判断角色名是否为超级管理员角色名
     *
     * @param roleName 角色名
     * @return 是否为超级管理员角色名
     */
    public static boolean isSuperAdmin(String roleName) {
        return SUPER_ADMIN.code.equals(roleName);
    }

    /**
     * 判断角色名是否为平台管理员角色名
     *
     * @param roleName 角色名
     * @return 是否为平台管理员角色名
     */
    public static boolean isClientAdmin(String roleName) {
        return CLIENT_ADMIN.code.equals(roleName);
    }

    /**
     * 判断角色名是否为租户管理员角色名
     *
     * @param roleName 角色名
     * @return 是否为租户管理员角色名
     */
    public static boolean isTenantAdmin(String roleName) {
        return TENANT_ADMIN.code.equals(roleName);
    }
}
