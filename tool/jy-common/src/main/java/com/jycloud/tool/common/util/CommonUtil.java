package com.jycloud.tool.common.util;

import com.jycloud.tool.common.constant.StringPool;
import com.jycloud.tool.common.function.CacheSelector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
public class CommonUtil {

    /**
     * 缓存查询模板
     *
     * @param cacheSelector    查询缓存的方法
     * @param databaseSelector 数据库查询方法
     * @return T
     */
    public static <T> T selectCacheByTemplate(CacheSelector<T> cacheSelector, Supplier<T> databaseSelector) {
        try {
            log.debug("query data from redis ······");
            // 先查 Redis缓存
            T t = cacheSelector.select();
            if (t == null) {
                // 没有记录再查询数据库
                return databaseSelector.get();
            } else {
                return t;
            }
        } catch (Exception e) {
            // 缓存查询出错，则去数据库查询
            log.error("redis error：", e);
            log.debug("query data from database ······");
            return databaseSelector.get();
        }
    }

    /**
     * 加密字符串
     *
     * @param str        需要加密的字符串
     * @param encryptKey 秘钥key
     * @return
     */
    public static String encryptStr(String str, String encryptKey) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(encryptKey);
            return encryptUtil.encrypt(str);
        } catch (Exception e) {
            log.info("encryptUtil加密失败：", e);
            return null;
        }
    }

    /**
     * 解密字符串
     *
     * @param encryptStr 需要解密的加密字符串
     * @param encryptKey 秘钥key
     * @return
     */
    public static String decryptStr(String encryptStr, String encryptKey) {
        try {
            EncryptUtil encryptUtil = new EncryptUtil(encryptKey);
            return encryptUtil.decrypt(encryptStr);
        } catch (Exception e) {
            log.info("encryptUtil解密失败：", e);
            return null;
        }
    }

    /**
     * 驼峰转下划线
     *
     * @param value 待转换值
     * @return 结果
     */
    public static String camelToUnderscore(String value) {
        if (StringUtils.isBlank(value))
            return value;
        String[] arr = StringUtils.splitByCharacterTypeCamelCase(value);
        if (arr.length == 0)
            return value;
        StringBuilder result = new StringBuilder();
        IntStream.range(0, arr.length).forEach(i -> {
            if (StringPool.UNDERSCORE.equals(arr[i])) {
                return;
            }
            if (i != arr.length - 1)
                result.append(arr[i]).append(StringPool.UNDERSCORE);
            else
                result.append(arr[i]);
        });
        return StringUtils.lowerCase(result.toString());
    }

    public static void main(String[] args) throws Exception {

        System.out.println(UUIDv7Util.generate());
    }
}
