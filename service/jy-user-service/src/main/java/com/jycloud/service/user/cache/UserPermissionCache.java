package com.jycloud.service.user.cache;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.jycloud.api.user.constant.UcConstant.USER_PERMISSION_CACHE_PREFIX;

/**
 * 用户权限缓存（权限标识集合 perms）
 * <p>
 * 与租户权限版本号（{@link PermissionVersionCache}）联动：写入时记录版本，
 * 读取时版本不一致即视为缓存失效，回源重算（惰性失效）。
 * key: uc:cache:user:permission:{userId}:{tenantId}
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserPermissionCache {

    private final RedisCacheHelper cacheHelper;

    private static final long REDIS_TIMEOUT = 86400L;
    private static final String VERSION_FIELD = "version";
    private static final String PERMS_FIELD = "perms";

    private String getRedisKey(String userId, String tenantId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(tenantId)) {
            return null;
        }
        return USER_PERMISSION_CACHE_PREFIX + userId + ":" + tenantId;
    }

    /**
     * 写入用户权限标识集合缓存并记录当前权限版本号
     *
     * @param perms    权限标识集合
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param version  当前租户权限版本号
     */
    public void cachePermissions(Set<String> perms, String userId, String tenantId, long version) {
        if (perms == null || perms.isEmpty()) {
            return;
        }
        String key = getRedisKey(userId, tenantId);
        if (key == null) {
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put(VERSION_FIELD, version);
        data.put(PERMS_FIELD, perms);
        cacheHelper.set(key, JSON.toJSONString(data), REDIS_TIMEOUT, TimeUnit.SECONDS);
        log.debug("{} cache user permissions: {}/{} version: {}", this.getClass().getSimpleName(), userId, tenantId, version);
    }

    /**
     * 读取用户权限标识集合缓存，缓存版本号与当前不一致时返回 null 触发回源
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param version  当前租户权限版本号
     * @return 权限标识集合；缓存缺失或版本不一致时返回 null
     */
    public Set<String> getPermissionsByUserId(String userId, String tenantId, long version) {
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
        return json.getJSONArray(PERMS_FIELD).toJavaList(String.class).stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
    }

    /**
     * 删除用户在指定租户下的权限缓存（单独设置用户角色/权限时精准失效）
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
        log.debug("{} evict user permission: {}/{}", this.getClass().getSimpleName(), userId, tenantId);
    }
}
