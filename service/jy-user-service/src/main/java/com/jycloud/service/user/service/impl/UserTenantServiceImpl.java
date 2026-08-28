package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.jycloud.api.user.entity.Tenant;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.entity.UserAuthority;
import com.jycloud.api.user.entity.UserDataPermission;
import com.jycloud.api.user.entity.UserDept;
import com.jycloud.api.user.entity.UserTenant;
import com.jycloud.api.user.vo.UserTenantVO;
import com.jycloud.service.user.cache.UserMenuCache;
import com.jycloud.service.user.cache.UserPermissionCache;
import com.jycloud.service.user.cache.UserRoleCache;
import com.jycloud.service.user.repository.mapper.UserMapper;
import com.jycloud.service.user.repository.mapper.UserTenantMapper;
import com.jycloud.service.user.service.TenantService;
import com.jycloud.service.user.service.UserAuthorityService;
import com.jycloud.service.user.service.UserDataPermissionService;
import com.jycloud.service.user.service.UserDeptService;
import com.jycloud.service.user.service.UserTenantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.exception.SysException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jycloud.api.user.constant.UcConstant.SUPER_ADMIN_ID;

/**
 * <p>
 * 用户租户表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTenantServiceImpl extends ServiceImpl<UserTenantMapper, UserTenant> implements UserTenantService {

    private final UserMapper userMapper;
    private final TenantService tenantService;
    private final UserAuthorityService userAuthorityService;
    private final UserDataPermissionService userDataPermissionService;
    private final UserDeptService userDeptService;
    private final UserRoleCache userRoleCache;
    private final UserPermissionCache userPermissionCache;
    private final UserMenuCache userMenuCache;

    @Override
    public List<UserTenantVO> listUserTenantVOs(Long userId) {
        return baseMapper.selectUserTenantVOList(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setTenant(UserTenant userTenant) {
        if (userTenant == null || userTenant.getUserId() == null || userTenant.getTenantId() == null) {
            log.warn("setTenant 参数为空: userTenant={}", userTenant);
            throw new SysException("参数不能为空");
        }

        Long userId = userTenant.getUserId();
        String tenantId = userTenant.getTenantId();

        if (SUPER_ADMIN_ID.equals(String.valueOf(userId))) {
            throw new SysException("超级管理员账号禁止操作");
        }

        boolean isDefault = userTenant.getIfDefault() != null && userTenant.getIfDefault() == 1;

        // 1. 先查是否已存在关联记录
        UserTenant existing = this.getOne(new LambdaQueryWrapper<UserTenant>()
                .eq(UserTenant::getUserId, userId)
                .eq(UserTenant::getTenantId, tenantId));

        if (existing != null) {
            // 更新：只更新 ifDefault，避免覆盖其他字段为 null
            if (isDefault) {
                // 先取消该用户其他租户的默认标记
                cancelOtherDefaults(userId, tenantId);
            }
            existing.setIfDefault(userTenant.getIfDefault());
            return this.updateById(existing);
        }

        // 2. 新增：设置默认值和默认租户逻辑
        userTenant.setConsumer(userTenant.getConsumer() != null ? userTenant.getConsumer() : 0);
        userTenant.setManager(userTenant.getManager() != null ? userTenant.getManager() : 0);
        userTenant.setStatus(userTenant.getStatus() != null ? userTenant.getStatus() : 1);

        if (isDefault) {
            cancelOtherDefaults(userId, tenantId);
        }

        return this.save(userTenant);
    }

    /**
     * 取消用户其他租户的默认标记
     */
    private void cancelOtherDefaults(Long userId, String excludeTenantId) {
        this.update(
                new LambdaUpdateWrapper<UserTenant>()
                        .set(UserTenant::getIfDefault, 0)
                        .eq(UserTenant::getUserId, userId)
                        .ne(UserTenant::getTenantId, excludeTenantId)
                        .eq(UserTenant::getIfDefault, 1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserFromTenant(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            log.warn("removeUserFromTenant 参数为空: userId={}, tenantId={}", userId, tenantId);
            throw new SysException("参数不能为空");
        }
        // 超级管理员账号禁止被移出租户
        if (SUPER_ADMIN_ID.equals(String.valueOf(userId))) {
            throw new SysException("超级管理员账号禁止操作");
        }

        // 校验用户与租户的关联存在
        UserTenant userTenant = this.getOne(new LambdaQueryWrapper<UserTenant>()
                .eq(UserTenant::getUserId, userId)
                .eq(UserTenant::getTenantId, tenantId));
        if (userTenant == null) {
            throw new SysException("该用户未关联当前租户");
        }

        // 1. 移除租户关系
        this.removeById(userTenant.getId());

        // 2. 移除该用户在租户下的全部权限（角色/资源关联、数据权限、部门关联及缓存）
        this.removeUserPermissions(userId, tenantId);

        log.info("用户已移出租户: userId={}, tenantId={}", userId, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserPermissions(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            log.warn("removeUserPermissions 参数为空: userId={}, tenantId={}", userId, tenantId);
            throw new SysException("参数不能为空");
        }
        // 超级管理员账号禁止操作
        if (SUPER_ADMIN_ID.equals(String.valueOf(userId))) {
            throw new SysException("超级管理员账号禁止操作");
        }

        // 1. 删除该用户在租户下的角色/资源关联
        userAuthorityService.remove(new LambdaQueryWrapper<UserAuthority>()
                .eq(UserAuthority::getUserId, userId)
                .eq(UserAuthority::getTenantId, tenantId));

        // 2. 删除该用户在租户下的数据权限
        userDataPermissionService.remove(new LambdaQueryWrapper<UserDataPermission>()
                .eq(UserDataPermission::getUserId, userId)
                .eq(UserDataPermission::getTenantId, tenantId));

        // 3. 删除该用户在租户下的部门关联
        userDeptService.remove(new LambdaQueryWrapper<UserDept>()
                .eq(UserDept::getUserId, userId)
                .eq(UserDept::getTenantId, tenantId));

        // 4. 清理该用户在租户下的角色/权限/菜单缓存
        String uid = String.valueOf(userId);
        userRoleCache.del(uid, tenantId);
        userPermissionCache.del(uid, tenantId);
        userMenuCache.delUserMenus(uid, tenantId);

        log.info("用户权限已移除: userId={}, tenantId={}", userId, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUserToTenant(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            log.warn("bindUserToTenant 参数为空: userId={}, tenantId={}", userId, tenantId);
            throw new SysException("参数不能为空");
        }
        // 超级管理员账号禁止被邀请
        if (SUPER_ADMIN_ID.equals(String.valueOf(userId))) {
            throw new SysException("超级管理员账号禁止操作");
        }

        // 校验用户存在且未被禁用（@TableLogic 自动过滤已删除用户）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new SysException(ResponseCode.USER_NOT_EXIST);
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new SysException("该用户已被禁用,无法邀请");
        }

        // 校验租户存在且启用
        Tenant tenant = tenantService.getTenantByTenantId(tenantId);
        if (tenant == null) {
            throw new SysException("租户不存在");
        }
        if (tenant.getStatus() == null || tenant.getStatus() != 1) {
            throw new SysException("该租户已被禁用");
        }

        // 重复关联校验（配合唯一索引 uk_tenant_user 双保险）
        UserTenant existing = this.getOne(new LambdaQueryWrapper<UserTenant>()
                .eq(UserTenant::getUserId, userId)
                .eq(UserTenant::getTenantId, tenantId));
        if (existing != null) {
            throw new SysException("该用户已关联当前租户");
        }

        // 建立关联：默认 B 端用户身份，非默认租户，状态正常
        UserTenant userTenant = new UserTenant();
        userTenant.setUserId(userId);
        userTenant.setTenantId(tenantId);
        userTenant.setIfDefault(0);
        userTenant.setConsumer(0);
        userTenant.setManager(1);
        userTenant.setStatus(1);
        this.save(userTenant);

        log.info("用户已加入租户: userId={}, tenantId={}", userId, tenantId);
    }

}
