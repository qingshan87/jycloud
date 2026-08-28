package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycloud.api.user.constant.AuthorityCategoryEnum;
import com.jycloud.api.user.entity.UserAuthority;
import com.jycloud.service.user.cache.UserMenuCache;
import com.jycloud.service.user.cache.UserPermissionCache;
import com.jycloud.service.user.cache.UserRoleCache;
import com.jycloud.service.user.repository.mapper.UserAuthorityMapper;
import com.jycloud.service.user.service.UserAuthorityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户权限关联表 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Service
@RequiredArgsConstructor
public class UserAuthorityServiceImpl extends ServiceImpl<UserAuthorityMapper, UserAuthority> implements UserAuthorityService {

    private final UserRoleCache userRoleCache;
    private final UserPermissionCache userPermissionCache;
    private final UserMenuCache userMenuCache;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setUserAuthorities(Long userId, String tenantId, Integer category, Set<Long> dataIds) {
        if (userId == null || StringUtils.isBlank(tenantId) || !AuthorityCategoryEnum.isValid(category)) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }

        // 删除原有关联
        this.remove(new LambdaQueryWrapper<UserAuthority>()
                .eq(UserAuthority::getUserId, userId)
                .eq(UserAuthority::getTenantId, tenantId)
                .eq(UserAuthority::getCategory, category));

        // 单独设置用户角色/权限，精准删除该用户的角色/权限/菜单缓存
        String uid = String.valueOf(userId);
        if (AuthorityCategoryEnum.ROLE.getCode().equals(category)) {
            userRoleCache.del(uid, tenantId);
        }
        userPermissionCache.del(uid, tenantId);
        userMenuCache.delUserMenus(uid, tenantId);

        // 保存新的关联
        if (dataIds == null || dataIds.isEmpty()) {
            return;
        }
        List<UserAuthority> authorities = dataIds.stream()
                .filter(Objects::nonNull)
                .map(dataId -> {
                    UserAuthority authority = new UserAuthority();
                    authority.setUserId(userId);
                    authority.setTenantId(tenantId);
                    authority.setCategory(category);
                    authority.setDataId(dataId);
                    return authority;
                })
                .collect(Collectors.toList());
        if (!authorities.isEmpty()) {
            this.saveBatch(authorities);
        }
    }

    @Override
    public Set<Long> listDataIds(Long userId, String tenantId, Integer category) {
        if (userId == null || StringUtils.isBlank(tenantId) || !AuthorityCategoryEnum.isValid(category)) {
            return Collections.emptySet();
        }
        return this.list(new LambdaQueryWrapper<UserAuthority>()
                .eq(UserAuthority::getUserId, userId)
                .eq(UserAuthority::getTenantId, tenantId)
                .eq(UserAuthority::getCategory, category))
                .stream()
                .map(UserAuthority::getDataId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
