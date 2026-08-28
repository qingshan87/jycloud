package com.jycloud.service.user.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jycloud.api.user.entity.Role;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.jycloud.api.user.constant.UcConstant.USER_ROLE_CACHE_PREFIX;

/**
 * 用户角色缓存
 * <p>
 * 缓存的是用户在指定租户下关联的启用角色对象列表（category=0 -> t_user_authority -> t_role），
 * 直接缓存 Role 对象，用户菜单组装与鉴权角色查询均可全缓存命中。
 * 与租户权限版本号（{@link PermissionVersionCache}）联动：写入时记录版本，
 * 读取时版本不一致即视为缓存失效，回源重算（惰性失效）。
 * key: uc:cache:user:role:{userId}:{tenantId}
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserRoleCache {

    private final RedisCacheHelper cacheHelper;

    private static final long REDIS_TIMEOUT = 86400L;
    private static final String VERSION_FIELD = "version";
    private static final String ROLES_FIELD = "roles";

    private String getRedisKey(String userId, String tenantId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(tenantId)) {
            return null;
        }
        return USER_ROLE_CACHE_PREFIX + userId + ":" + tenantId;
    }

    /**
     * 写入用户角色对象列表缓存并记录当前权限版本号
     *
     * @param roles    角色列表
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param version  当前租户权限版本号
     */
    public void cacheUserRoles(List<Role> roles, String userId, String tenantId, long version) {
        if (roles == null || roles.isEmpty()) {
            return;
        }
        String key = getRedisKey(userId, tenantId);
        if (key == null) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put(VERSION_FIELD, version);
        data.put(ROLES_FIELD, roles);
        cacheHelper.set(key, JSON.toJSONString(data), REDIS_TIMEOUT, TimeUnit.SECONDS);
        log.debug("{} cache user roles: {}/{} version: {}", this.getClass().getSimpleName(), userId, tenantId, version);
    }

    /**
     * 读取用户角色对象列表缓存，缓存版本号与当前不一致时返回 null 触发回源
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param version  当前租户权限版本号
     * @return 角色列表；缓存缺失或版本不一致时返回 null
     */
    public List<Role> getUserRolesByUserId(String userId, String tenantId, long version) {
        String key = getRedisKey(userId, tenantId);
        if (key == null) {
            return null;
        }
        Object cached = cacheHelper.get(key);
        if (cached == null) {
            return null;
        }
        JSONObject json = JSON.parseObject((String) cached);
        if (json == null || json.getLongValue(VERSION_FIELD) != version) {
            return null;
        }
        JSONArray roles = json.getJSONArray(ROLES_FIELD);
        if (roles == null) {
            return null;
        }
        return roles.toJavaList(Role.class);
    }

    /**
     * 删除用户在指定租户下的角色缓存
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     */
    public void del(String userId, String tenantId) {
        String key = getRedisKey(userId, tenantId);
        if (key == null) {
            return;
        }
        cacheHelper.delete(key);
        log.debug("{} evict user role: {}/{}", this.getClass().getSimpleName(), userId, tenantId);
    }
}
