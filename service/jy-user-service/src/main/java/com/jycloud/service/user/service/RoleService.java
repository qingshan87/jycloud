package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jycloud.api.user.constant.AdminRoleNameEnum;
import com.jycloud.api.user.dto.RoleDTO;
import com.jycloud.api.user.dto.RoleQueryDTO;
import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
public interface RoleService extends IService<Role> {

    /**
     * 新增角色（租户内角色名唯一）
     *
     * @param roleDTO 角色实体
     * @return 保存后的角色
     */
    boolean createRole(RoleDTO roleDTO);

    /**
     * 修改角色
     *
     * @param roleDTO
     * @return
     */
    boolean updateRole(RoleDTO roleDTO);

    /**
     * 新增管理员角色（不允许创建超级管理员角色）
     *
     * @param roleDTO 角色实体
     * @return 是否成功
     */
    boolean createAdminRole(RoleDTO roleDTO);

    /**
     * 修改管理员角色（不允许变更角色名与租户）
     *
     * @param roleDTO 角色实体
     * @return 是否成功
     */
    boolean updateAdminRole(RoleDTO roleDTO);

    /**
     * 分页查询角色列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<RoleVO> pageByCondition(RoleQueryDTO queryDTO);

    /**
     * 根据租户ID查询角色列表,不包含内置角色super_admin
     *
     * @param tenantId 租户ID
     * @param readonly 是否是内置角色, 不传查询租户所有角色
     * @return 角色列表
     */
    List<RoleVO> listByTenantId(String tenantId, Integer readonly);

    /**
     * 删除角色（内置角色不允许删除）
     *
     * @param roleId 角色 ID
     */
    void deleteRole(Long roleId);

    /**
     * 角色详情（含绑定的菜单ID集合）
     *
     * @param roleId 角色 ID
     * @return 角色详情
     */
    RoleVO detail(Long roleId);

}
