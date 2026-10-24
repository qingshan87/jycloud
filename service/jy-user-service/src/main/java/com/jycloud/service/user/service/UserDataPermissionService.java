package com.jycloud.service.user.service;

import com.jycloud.api.user.entity.UserDataPermission;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户数据权限表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface UserDataPermissionService extends IService<UserDataPermission> {

    /**
     * 设置用户数据权限（当前租户，先删后插；dept_id 字段预留，不在此维护）
     *
     * @param userId    用户 ID
     * @param dataScope 数据权限范围：all（全部）、dept（部门）、myself（自己）
     */
    void setDataPermission(Long userId, String dataScope);

    /**
     * 查询用户在指定租户下的数据权限
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 用户数据权限，无则返回 null
     */
    UserDataPermission getDataPermission(Long userId, String tenantId);
}
