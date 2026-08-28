package com.jycloud.service.user.cache;

import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.jycloud.api.user.constant.UcConstant.USER_TOKEN_CACHE_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCache {
    private final RedisCacheHelper cacheHelper;

    private static final long REDIS_TIMEOUT = 86400L;

    // 缓存用户token
    public void cacheToken(String token, String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        String key = USER_TOKEN_CACHE_PREFIX + userId;
        cacheHelper.set(key, token, REDIS_TIMEOUT, TimeUnit.SECONDS);
    }

    // 获取用户token
    public String getToken(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        String key = USER_TOKEN_CACHE_PREFIX + userId;
        return cacheHelper.get(key);
    }

    // 删除用户token
    public void deleteToken(String userId) {
        if (StringUtils.isBlank(userId)) {
            return;
        }
        String key = USER_TOKEN_CACHE_PREFIX + userId;
        cacheHelper.delete(key);
    }

}
