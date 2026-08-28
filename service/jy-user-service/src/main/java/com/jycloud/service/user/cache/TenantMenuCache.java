package com.jycloud.service.user.cache;


import com.alibaba.fastjson2.JSON;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.jycloud.api.user.constant.UcConstant.TENANT_MENU_CACHE_PREFIX;

/**
 * 租户菜单缓存
 * <p>
 * 缓存的是租户管理员的 menu 数据，即该租户拥有的全部 menu 数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantMenuCache {
    private final RedisCacheHelper cacheHelper;

    private static final long REDIS_TIMEOUT = 86400L;

    private String getRedisKey(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return null;
        }
        return TENANT_MENU_CACHE_PREFIX + tenantId;
    }

    public void cacheTenantMenus(List<Menu> list, String tenantId) {
        if (list == null || list.isEmpty()) {
            return;
        }
        String key = getRedisKey(tenantId);
        cacheHelper.set(key, JSON.toJSONString(list), REDIS_TIMEOUT, TimeUnit.SECONDS);
        log.debug("{} cache tenantId: {}", this.getClass().getSimpleName(), tenantId);
    }

    public List<Menu> getTenantMenusByTenantId(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return null;
        }
        Object cached = cacheHelper.get(getRedisKey(tenantId));
        if (cached == null) {
            return null;
        }
        return JSON.parseArray((String) cached, Menu.class);
    }

    public void delTenantMenus(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return;
        }
        cacheHelper.delete(getRedisKey(tenantId));
        log.debug("{} remove tenantId: {}", this.getClass().getSimpleName(), tenantId);
    }

}
