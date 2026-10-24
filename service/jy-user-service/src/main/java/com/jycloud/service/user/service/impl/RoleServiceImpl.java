package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.AdminRoleNameEnum;
import com.jycloud.api.user.dto.RoleDTO;
import com.jycloud.api.user.dto.RoleQueryDTO;
import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.vo.RoleVO;
import com.jycloud.service.user.cache.PermissionVersionCache;
import com.jycloud.service.user.cache.TenantMenuCache;
import com.jycloud.service.user.repository.mapper.RoleMapper;
import com.jycloud.service.user.service.RoleMenuService;
import com.jycloud.service.user.service.RoleService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final RoleMenuService roleMenuService;
    private final TenantMenuCache tenantMenuCache;
    private final PermissionVersionCache permissionVersionCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(RoleDTO roleDTO) {
        if (roleDTO == null || StringUtils.isBlank(roleDTO.getRoleName())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        // 普通角色不允许使用内置角色名
        if (AdminRoleNameEnum.isValid(roleDTO.getRoleName())) {
            throw new SysException("角色名不能使用内置角色名");
        }

        String tenantId = UserContext.getTenantId();

        // 租户内角色名唯一
        if (checkRoleName(tenantId, roleDTO.getRoleName())) {
            throw new SysException("角色已存在,请勿重复创建");
        }

        Role role = new Role();
        role.setTenantId(tenantId);
        role.setRoleName(roleDTO.getRoleName());
        role.setRemark(roleDTO.getRemark());
        role.setSort(roleDTO.getSort());
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        role.setReadonly(0);
        this.save(role);

        // 更新角色权限
        roleMenuService.setRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAdminRole(RoleDTO roleDTO) {
        if (roleDTO == null || StringUtils.isBlank(roleDTO.getRoleName())) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        // 不允许创建超级管理员角色
        if (AdminRoleNameEnum.isSuperAdmin(roleDTO.getRoleName())) {
            throw new SysException("不允许创建超级管理员角色");
        }

        // 管理员角色名必须是预置名称
        if (!AdminRoleNameEnum.isValid(roleDTO.getRoleName())) {
            throw new SysException("管理员角色名不合法");
        }

        String tenantId = roleDTO.getTenantId();

        // 租户内角色名唯一
        if (checkRoleName(tenantId, roleDTO.getRoleName())) {
            throw new SysException("角色已存在,请勿重复创建");
        }

        Role role = new Role();
        role.setTenantId(tenantId);
        role.setRoleName(roleDTO.getRoleName());
        role.setRemark(roleDTO.getRemark());
        role.setSort(roleDTO.getSort());
        role.setStatus(roleDTO.getStatus() != null ? roleDTO.getStatus() : 1);
        role.setReadonly(1);
        this.save(role);

        // 更新角色权限
        roleMenuService.setRoleMenus(role.getRoleId(), roleDTO.getMenuIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleDTO roleDTO) {
        if (roleDTO == null || roleDTO.getRoleId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        Role role = getRoleOrThrow(roleDTO.getRoleId());

        // 管理员角色使用管理员角色更新接口
        if (isReadonly(role)) {
            throw new SysException("内置角色禁止编辑");
        }

        // 角色名变更校验
        if (StringUtils.isNotBlank(roleDTO.getRoleName())) {
            // 不能使用内置角色名
            if (AdminRoleNameEnum.isValid(roleDTO.getRoleName())) {
                throw new SysException("角色名不能使用内置角色名");
            }
            // 租户内唯一（排除自身）
            if (checkRoleName(role.getTenantId(), roleDTO.getRoleName(), roleDTO.getRoleId())) {
                throw new SysException("角色名已存在");
            }
        }

        update(roleDTO);
        permissionVersionCache.incr(role.getTenantId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminRole(RoleDTO roleDTO) {
        if (roleDTO == null || roleDTO.getRoleId() == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        Role role = getRoleOrThrow(roleDTO.getRoleId());

        // 仅允许更新管理员角色
        if (!isReadonly(role)) {
            throw new SysException("普通角色请使用普通角色更新接口");
        }

        // 不允许变更角色名和租户id
        roleDTO.setRoleName(null);
        roleDTO.setTenantId(null);

        update(roleDTO);

        // 管理员角色的菜单即租户全量菜单，变更后删除租户菜单缓存
        tenantMenuCache.delTenantMenus(role.getTenantId());
        permissionVersionCache.incr(role.getTenantId());

        return true;
    }

    // 更新公共方法
    private void update(RoleDTO roleDTO) {
        Role updateRole = new Role();
        updateRole.setRoleId(roleDTO.getRoleId());
        updateRole.setRoleName(roleDTO.getRoleName());
        updateRole.setRemark(roleDTO.getRemark());
        updateRole.setSort(roleDTO.getSort());
        updateRole.setStatus(roleDTO.getStatus());
        this.updateById(updateRole);

        // 更新角色权限
        roleMenuService.setRoleMenus(roleDTO.getRoleId(), roleDTO.getMenuIds());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRole(Long roleId) {
        if (roleId == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        Role role = getRoleOrThrow(roleId);

        // 内置角色不允许删除
        if (isReadonly(role)) {
            throw new SysException("内置角色不允许删除");
        }

        // 逻辑删除（在 Mapper 层完成）
        String currentUserId = UserContext.getUserId();
        Long deleteUid = StringUtils.isNotBlank(currentUserId) ? Long.valueOf(currentUserId) : null;
        this.baseMapper.deleteById(roleId, deleteUid);
        permissionVersionCache.incr(role.getTenantId());
    }

    @Override
    public RoleVO detail(Long roleId) {
        Role role = getRoleOrThrow(roleId);
        RoleVO vo = DtoEntityUtil.trans(role, RoleVO.class);
        vo.setMenuIds(roleMenuService.listMenuIdsByRoleId(roleId));
        return vo;
    }

    @Override
    public IPage<RoleVO> pageByCondition(RoleQueryDTO queryDTO) {
        Page<Role> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "roleId", SortUtil.ORDER_DESC, true);

        LambdaQueryWrapper<Role> qw = new LambdaQueryWrapper<>();
        qw.eq(StringUtils.isNotBlank(queryDTO.getTenantId()), Role::getTenantId, queryDTO.getTenantId())
                .like(StringUtils.isNotBlank(queryDTO.getRoleName()), Role::getRoleName, queryDTO.getRoleName())
                .eq(queryDTO.getReadonly() != null, Role::getReadonly, queryDTO.getReadonly())
                .eq(queryDTO.getStatus() != null, Role::getStatus, queryDTO.getStatus());

        return this.page(page, qw).convert(role -> DtoEntityUtil.trans(role, RoleVO.class));
    }

    @Override
    public List<RoleVO> listByTenantId(String tenantId, Integer readonly) {
        tenantId = StringUtils.isNotBlank(tenantId) ? tenantId : UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return List.of();
        }
        LambdaQueryWrapper<Role> qw = new LambdaQueryWrapper<>();
        if (readonly != null){
            qw.eq(Role::getReadonly, readonly);
        }
        qw.eq(Role::getTenantId, tenantId);
        qw.orderByAsc(Role::getSort);
        qw.orderByAsc(Role::getRoleId);
        List<Role> roleList = this.list(qw);
        return roleList.stream()
                .filter(role -> !AdminRoleNameEnum.isSuperAdmin(role.getRoleName()))
                .map(role -> DtoEntityUtil.trans(role, RoleVO.class))
                .collect(Collectors.toList());
    }


    // ======================== 私有方法 ========================

    /**
     * 根据 ID 查询角色，不存在则抛出异常
     */
    private Role getRoleOrThrow(Long roleId) {
        Role role = this.getById(roleId);
        if (role == null) {
            throw new SysException(ResponseCode.CHECK_NO_DATA);
        }
        return role;
    }

    /**
     * 是否为内置角色
     */
    private boolean isReadonly(Role role) {
        return role.getReadonly() != null && role.getReadonly() == 1;
    }


    /**
     * 检查角色名是否已存在（排除自身，用于更新）
     *
     * @param tenantId 租户 ID
     * @param roleName 角色名
     * @param roleId   需要排除的角色 ID
     * @return 是否存在
     */
    private boolean checkRoleName(String tenantId, String roleName, Long roleId) {
        LambdaQueryWrapper<Role> qw = new LambdaQueryWrapper<>();
        qw.eq(Role::getRoleName, roleName);
        qw.eq(Role::getTenantId, tenantId);
        if (roleId != null) {
            qw.ne(Role::getRoleId, roleId);
        }
        long count = this.count(qw);
        return count > 0;
    }

    private boolean checkRoleName(String tenantId, String roleName) {
        return checkRoleName(tenantId, roleName, null);
    }
}
