package com.jycloud.tool.cache.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 缓存通用操作工具 —— 封装 RedisTemplate 常用方法
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheHelper {

    private final RedisTemplate<String, Object> redisTemplate;

    // ======================== Key-Value 基础操作 ========================

    /**
     * 写入缓存（无过期时间）
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis set error, key: {}", key, e);
        }
    }

    /**
     * 写入缓存并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            log.error("Redis set error, key: {}", key, e);
        }
    }

    /**
     * 写入缓存并设置过期时间（Duration 版本）
     */
    public void set(String key, Object value, Duration duration) {
        try {
            redisTemplate.opsForValue().set(key, value, duration);
        } catch (Exception e) {
            log.error("Redis set error, key: {}", key, e);
        }
    }

    /**
     * 读取缓存（返回指定类型）
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            return (T) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error, key: {}", key, e);
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete error, key: {}", key, e);
            return false;
        }
    }

    /**
     * 批量删除缓存
     */
    public long delete(Collection<String> keys) {
        try {
            Long count = redisTemplate.delete(keys);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Redis batch delete error", e);
            return 0L;
        }
    }

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis hasKey error, key: {}", key, e);
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("Redis expire error, key: {}", key, e);
            return false;
        }
    }

    /**
     * 获取剩余过期时间（秒），-1 永不过期，-2 不存在
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key);
            return expire != null ? expire : -2L;
        } catch (Exception e) {
            log.error("Redis getExpire error, key: {}", key, e);
            return -2L;
        }
    }

    // ======================== Cache-Aside 模式 ========================

    /**
     * 缓存穿透保护 —— 缓存有则返回，否则查数据库并回写缓存
     *
     * @param key         缓存 key
     * @param supplier    数据库查询
     * @param timeout     缓存过期时间
     * @param unit        时间单位
     */
    public <T> T getOrFetch(String key, Supplier<T> supplier, long timeout, TimeUnit unit) {
        try {
            T cached = get(key);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            log.warn("Redis getOrFetch read cache error, fallback to db, key: {}", key, e);
        }
        T dbResult = supplier.get();
        if (dbResult != null) {
            set(key, dbResult, timeout, unit);
        }
        return dbResult;
    }

    // ======================== 自增/自减 ========================

    /**
     * 自增 delta 并返回新值
     */
    public long incr(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0L;
        } catch (Exception e) {
            log.error("Redis incr error, key: {}", key, e);
            return 0L;
        }
    }

    /**
     * 自减 delta 并返回新值
     */
    public long decr(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().decrement(key, delta);
            return result != null ? result : 0L;
        } catch (Exception e) {
            log.error("Redis decr error, key: {}", key, e);
            return 0L;
        }
    }

    // ======================== Hash 操作 ========================

    /**
     * Hash 写入单个字段
     */
    public void hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("Redis hSet error, key: {}, field: {}", key, field, e);
        }
    }

    /**
     * Hash 批量写入
     */
    public void hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            log.error("Redis hSetAll error, key: {}", key, e);
        }
    }

    /**
     * Hash 读取单个字段
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String field) {
        try {
            return (T) redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("Redis hGet error, key: {}, field: {}", key, field, e);
            return null;
        }
    }

    /**
     * Hash 读取全部字段
     */
    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis hGetAll error, key: {}", key, e);
            return Collections.emptyMap();
        }
    }

    /**
     * Hash 删除字段
     */
    public long hDelete(String key, Object... fields) {
        try {
            Long count = redisTemplate.opsForHash().delete(key, fields);
            return count != null ? count : 0L;
        } catch (Exception e) {
            log.error("Redis hDelete error, key: {}", key, e);
            return 0L;
        }
    }

    /**
     * Hash 判断字段是否存在
     */
    public boolean hHasKey(String key, String field) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, field));
        } catch (Exception e) {
            log.error("Redis hHasKey error, key: {}, field: {}", key, field, e);
            return false;
        }
    }

    // ======================== 批量操作 ========================

    /**
     * 批量读取（按传入 key 顺序返回，key 不存在则对应 null）
     */
    public List<Object> multiGet(Collection<String> keys) {
        try {
            return redisTemplate.opsForValue().multiGet(keys);
        } catch (Exception e) {
            log.error("Redis multiGet error", e);
            return Collections.emptyList();
        }
    }

    /**
     * 按 pattern 模糊删除（如 "user:*"）
     */
    public long deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                Long count = redisTemplate.delete(keys);
                return count != null ? count : 0L;
            }
            return 0L;
        } catch (Exception e) {
            log.error("Redis deleteByPattern error, pattern: {}", pattern, e);
            return 0L;
        }
    }
}
