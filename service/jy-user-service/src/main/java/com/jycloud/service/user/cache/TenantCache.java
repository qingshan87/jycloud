package com.jycloud.service.user.cache;

import com.alibaba.fastjson2.JSON;
import com.jycloud.api.user.entity.Tenant;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.jycloud.api.user.constant.UcConstant.TENANT_INFO_CACHE_PREFIX;

/**
 * 租户缓存 —— tenantId 存完整 Tenant
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantCache {

    private final RedisCacheHelper cacheHelper;

    private static final long REDIS_TIMEOUT = 86400L;

    /**
     * 缓存完整租户信息 —— tenantId → 完整 Tenant
     */
    public void cacheTenant(Tenant tenant) {
        if (tenant == null || StringUtils.isBlank(tenant.getTenantId())) {
            return;
        }
        String key = TENANT_INFO_CACHE_PREFIX + tenant.getTenantId();
        cacheHelper.set(key, JSON.toJSONString(tenant), REDIS_TIMEOUT, TimeUnit.SECONDS);
        log.debug("{} cache tenantId: {}", this.getClass().getSimpleName(), tenant.getTenantId());
    }

    /**
     * 通过 tenantId 获取完整租户信息
     */
    public Tenant getTenantByTenantId(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return null;
        }
        Object cached = cacheHelper.get(TENANT_INFO_CACHE_PREFIX + tenantId);
        if (cached == null) {
            return null;
        }
        return JSON.parseObject((String) cached, Tenant.class);
    }

    /**
     * 删除租户缓存
     */
    public void delTenant(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return;
        }
        cacheHelper.delete(TENANT_INFO_CACHE_PREFIX + tenantId);
        log.debug("{} evict tenant cache: {}", this.getClass().getSimpleName(), tenantId);
    }

    /**
     * 删除租户缓存
     */
    public void delTenant(Tenant tenant) {
        if (tenant == null) {
            return;
        }
        delTenant(tenant.getTenantId());
    }

    /**
     * 判断 tenantId 缓存是否存在
     */
    public boolean hasTenant(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            return false;
        }
        return cacheHelper.hasKey(TENANT_INFO_CACHE_PREFIX + tenantId);
    }
}
