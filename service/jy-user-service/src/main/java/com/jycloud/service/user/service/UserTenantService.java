package com.jycloud.service.user.service;

import com.jycloud.api.user.entity.UserTenant;
import com.jycloud.api.user.vo.UserTenantVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户租户表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface UserTenantService extends IService<UserTenant> {

    /**
     * 查询用户关联的租户列表（含租户名称、状态）
     */
    List<UserTenantVO> listUserTenantVOs(Long userId);

    /**
     * 设置租户关联
     *
     */
    Boolean setTenant(UserTenant userTenant);

    /**
     * 将用户从指定租户移除
     * <p>
     * 删除用户与租户的关联记录，并级联清理该用户在该租户下的
     * 角色/资源关联、数据权限、部门关联及相关缓存。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     */
    void removeUserFromTenant(Long userId, String tenantId);

    /**
     * 移除用户在指定租户下的全部权限
     * <p>
     * 删除该用户在该租户下的角色/资源关联、数据权限、部门关联，
     * 并清理角色/权限/菜单缓存；不影响用户与租户的关联关系。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     */
    void removeUserPermissions(Long userId, String tenantId);

    /**
     * 将用户与指定租户建立关联（邀请用户加入租户）
     * <p>
     * 仅建立用户与租户的关联记录，不进行任何权限操作（角色/资源分配由管理员后续处理）。
     * 默认以 B 端用户身份加入（manager=1, consumer=0），非默认租户。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     */
    void bindUserToTenant(Long userId, String tenantId);
}
