package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.user.constant.DataScopeEnum;
import com.jycloud.api.user.entity.UserDataPermission;
import com.jycloud.service.user.repository.mapper.UserDataPermissionMapper;
import com.jycloud.service.user.service.UserDataPermissionService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 用户数据权限表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Service
public class UserDataPermissionServiceImpl extends ServiceImpl<UserDataPermissionMapper, UserDataPermission> implements UserDataPermissionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDataPermission(Long userId, String dataScope) {
        if (userId == null || !DataScopeEnum.isValid(dataScope)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            throw new SysException("租户ID不能为空");
        }

        // 先删后插，保证单条
        this.remove(new LambdaQueryWrapper<UserDataPermission>()
                .eq(UserDataPermission::getUserId, userId)
                .eq(UserDataPermission::getTenantId, tenantId));

        UserDataPermission permission = new UserDataPermission();
        permission.setUserId(userId);
        permission.setDataScope(dataScope);
        permission.setTenantId(tenantId);
        // dept_id 字段预留，部门以用户部门关联为准
        this.save(permission);
        log.info("用户数据权限已设置: userId={}, dataScope={}, tenantId={}", userId, dataScope, tenantId);
    }

    @Override
    public UserDataPermission getDataPermission(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return null;
        }
        return this.getOne(new LambdaQueryWrapper<UserDataPermission>()
                .eq(UserDataPermission::getUserId, userId)
                .eq(UserDataPermission::getTenantId, tenantId));
    }
}
