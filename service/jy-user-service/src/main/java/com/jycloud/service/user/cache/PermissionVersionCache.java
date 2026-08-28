package com.jycloud.service.user.cache;

import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.jycloud.api.user.constant.UcConstant.PERM_VERSION_CACHE_PREFIX;

/**
 * 权限缓存版本号
 * <p>
 * 仅存储在 Redis，不入库，key 不设 TTL。权限数据变更时自增版本号，
 * 读侧通过版本比对决定是否回源重算（惰性失效）。
 * 版本号惰性初始化：key 不存在或值非法时初始化为 1，避免 -1 哨兵值写入各缓存。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PermissionVersionCache {

    private final RedisCacheHelper cacheHelper;

    /**
     * 权限数据变更后自增租户权限版本号
     *
     * @param tenantId 租户 ID
     */
    public void incr(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return;
        }
        cacheHelper.incr(PERM_VERSION_CACHE_PREFIX + tenantId, 1L);
    }

    /**
     * 获取租户权限版本号；key 不存在或值非法时初始化为 1 并返回
     *
     * @param tenantId 租户 ID
     * @return 版本号（正常场景恒大于等于 1）
     */
    public long getVersion(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return 0L;
        }
        Object value = cacheHelper.get(PERM_VERSION_CACHE_PREFIX + tenantId);
        if (value != null) {
            try {
                long version = Long.parseLong(String.valueOf(value));
                if (version >= 1) {
                    return version;
                }
                log.warn("权限版本号非法: {}", value);
            } catch (NumberFormatException e) {
                log.warn("权限版本号非法: {}", value);
            }
        }
        // 版本号未初始化或非法：原子初始化为 1，保证版本 key 恒存在、版本号恒大于等于 1
        return cacheHelper.incr(PERM_VERSION_CACHE_PREFIX + tenantId, 1L);
    }
}
