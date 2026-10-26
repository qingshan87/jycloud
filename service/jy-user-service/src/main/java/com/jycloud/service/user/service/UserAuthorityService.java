package com.jycloud.service.user.service;

import com.jycloud.api.user.entity.UserAuthority;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.Set;

/**
 * <p>
 * 用户权限关联表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface UserAuthorityService extends IService<UserAuthority> {

    /**
     * 设置用户权限关联（先删后插）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param category 类型 0角色(role_id) 1资源(menu_id)
     * @param dataIds  数据 ID 集合（角色 ID 或菜单 ID）
     */
    void setUserAuthorities(Long userId, String tenantId, Integer category, Set<Long> dataIds);

    /**
     * 查询用户在指定类别下关联的数据 ID 集合
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param category 类型 0角色(role_id) 1资源(menu_id)
     * @return 数据 ID 集合
     */
    Set<Long> listDataIds(Long userId, String tenantId, Integer category);
}
