package com.jycloud.service.user.service;

import com.jycloud.api.user.entity.UserDept;
import com.baomidou.mybatisplus.spring.service.IService;

/**
 * <p>
 * 用户部门表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface UserDeptService extends IService<UserDept> {

    /**
     * 设置用户部门（当前租户，先删后插，单部门）
     *
     * @param userId 用户 ID
     * @param deptId 部门 ID，为空表示解绑部门
     */
    void setUserDept(Long userId, Long deptId);

    /**
     * 查询用户在指定租户下的部门关联
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 用户部门关联，无则返回 null
     */
    UserDept getUserDept(Long userId, String tenantId);

    /**
     * 判断部门是否已绑定用户
     *
     * @param deptId 部门 ID
     * @return 是否已绑定
     */
    boolean existsByDeptId(Long deptId);
}
