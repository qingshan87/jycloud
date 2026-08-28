package com.jycloud.service.user.cache;

import com.alibaba.fastjson2.JSON;
import com.jycloud.api.user.entity.User;
import com.jycloud.tool.cache.helper.RedisCacheHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.jycloud.api.user.constant.UcConstant.*;

/**
 * 用户缓存 —— userId 存完整 User，其他维度存 userId 索引
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserCache {

    private final RedisCacheHelper cacheHelper;

    private static final long REDIS_TIMEOUT = 86400L;

    // ======================== 写入缓存 ========================

    /**
     * 缓存完整用户信息 —— userId → 完整 User；name/mobile/idCard → userId
     */
    public void cacheUser(User user) {
        if (user == null) {
            return;
        }
        cacheUserById(user);
        cacheUserIdByUsername(user);
        cacheUserIdByMobile(user);
        cacheUserIdByIdCard(user);
        cacheUserIdByUnionid(user);
    }

    /**
     * userId → 完整 User 对象
     */
    private void cacheUserById(User user) {
        String key = USER_ID_CACHE_PREFIX + user.getUserId();
        String userJson = JSON.toJSONString(user);
        cacheHelper.set(key, userJson, REDIS_TIMEOUT, TimeUnit.SECONDS);
        log.debug("{} cache userId: {}", this.getClass().getSimpleName(), user.getUserId());
    }

    /**
     * username → userId（反向索引）
     */
    private void cacheUserIdByUsername(User user) {
        if (StringUtils.isBlank(user.getUsername())) {
            return;
        }
        String key = USER_NAME_CACHE_PREFIX + user.getUsername();
        cacheHelper.set(key, String.valueOf(user.getUserId()), REDIS_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * mobile → userId（反向索引）
     */
    private void cacheUserIdByMobile(User user) {
        if (StringUtils.isBlank(user.getMobile())) {
            return;
        }
        String key = USER_MOBILE_CACHE_PREFIX + user.getMobile();
        cacheHelper.set(key, String.valueOf(user.getUserId()), REDIS_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * idCard → userId（反向索引）
     */
    private void cacheUserIdByIdCard(User user) {
        if (StringUtils.isBlank(user.getIdCardNo())) {
            return;
        }
        String key = USER_ID_CARD_CACHE_PREFIX + user.getIdCardNo();
        cacheHelper.set(key, String.valueOf(user.getUserId()), REDIS_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * unionid → userId（反向索引）
     */
    private void cacheUserIdByUnionid(User user) {
        if (StringUtils.isBlank(user.getUnionid())) {
            return;
        }
        String key = USER_UNIONID_CACHE_PREFIX + user.getUnionid();
        cacheHelper.set(key, String.valueOf(user.getUserId()), REDIS_TIMEOUT, TimeUnit.SECONDS);
    }

    // ======================== 读取缓存 ========================

    /**
     * 通过 userId 获取完整用户数据
     */
    public User getUserById(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        String key = USER_ID_CACHE_PREFIX + userId;
        Object o = cacheHelper.get(key);
        return JSON.parseObject((String) o, User.class);
    }

    /**
     * 通过用户名获取用户 —— 先查 name→userId 索引，再查 userId→User
     */
    public User getUserByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return null;
        }
        String userId = getUserIdByKey(USER_NAME_CACHE_PREFIX, username);
        return userId != null ? getUserById(userId) : null;
    }

    /**
     * 通过手机号获取用户 —— 先查 mobile→userId 索引，再查 userId→User
     */
    public User getUserByMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return null;
        }
        String userId = getUserIdByKey(USER_MOBILE_CACHE_PREFIX, mobile);
        return userId != null ? getUserById(userId) : null;
    }

    /**
     * 通过 unionid 获取用户 —— 先查 unionid→userId 索引，再查 userId→User
     */
    public User getUserByUnionid(String unionid) {
        if (StringUtils.isBlank(unionid)) {
            return null;
        }
        String userId = getUserIdByKey(USER_UNIONID_CACHE_PREFIX, unionid);
        return userId != null ? getUserById(userId) : null;
    }

    /**
     * 通过身份证号获取用户 —— 先查 idCard→userId 索引，再查 userId→User
     */
    public User getUserByIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return null;
        }
        String userId = getUserIdByKey(USER_ID_CARD_CACHE_PREFIX, idCard);
        return userId != null ? getUserById(userId) : null;
    }

    /**
     * 从索引缓存中获取 userId
     */
    private String getUserIdByKey(String prefix, String value) {
        String key = prefix + value;
        Object cached = cacheHelper.get(key);
        if (cached == null) {
            return null;
        }
        return cached.toString();
    }

    // ======================== 删除缓存 ========================

    /**
     * 删除用户的所有缓存
     */
    public void delUser(User user) {
        if (user == null) {
            return;
        }
        cacheHelper.delete(USER_ID_CACHE_PREFIX + user.getUserId());

        if (StringUtils.isNotBlank(user.getUsername())) {
            cacheHelper.delete(USER_NAME_CACHE_PREFIX + user.getUsername());
        }
        if (StringUtils.isNotBlank(user.getMobile())) {
            cacheHelper.delete(USER_MOBILE_CACHE_PREFIX + user.getMobile());
        }
        if (StringUtils.isNotBlank(user.getIdCardNo())) {
            cacheHelper.delete(USER_ID_CARD_CACHE_PREFIX + user.getIdCardNo());
        }
        if (StringUtils.isNotBlank(user.getUnionid())) {
            cacheHelper.delete(USER_UNIONID_CACHE_PREFIX + user.getUnionid());
        }
        log.debug("{} evict user cache: {}", this.getClass().getSimpleName(), user.getUserId());
    }

    /**
     * 判断 userId 缓存是否存在
     */
    public boolean hasUserById(Long userId) {
        if (userId == null) {
            return false;
        }
        return cacheHelper.hasKey(USER_ID_CACHE_PREFIX + userId);
    }

}
