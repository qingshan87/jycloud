package com.jycloud.tool.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

@Slf4j
public final class AppUtil {

    private static final String CHAR_MAP =
            "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int CHAR_SET_SIZE = CHAR_MAP.length();
    private static final int DEFAULT_SHORT_KEY_LENGTH = 8;
    private static final int DEFAULT_MD5_ITERATIONS = 2;

    // ✅ 修复：用 Set 替代不存在的 isAnyOf
    private static final Set<String> SUPPORTED_ALGORITHMS = Set.of("MD5", "SHA-1", "SHA-256");

    private AppUtil() {}

    public static String generateAppKey() {
        return generateShortUuid(DEFAULT_SHORT_KEY_LENGTH);
    }

    public static String generateShortUuid(int length) {
        if (length < 1 || length > 16) {
            throw new IllegalArgumentException("Short key length must be between 1 and 16, but got: " + length);
        }

        StringBuilder shortBuffer = new StringBuilder(length);
        String uuid = (length <= 8)
                ? UUID.randomUUID().toString().replace("-", "")
                : UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");

        for (int i = 0; i < length; i++) {
            String hexGroup = uuid.substring(i * 4, i * 4 + 4);
            int value = Integer.parseInt(hexGroup, 16);
            shortBuffer.append(CHAR_MAP.charAt(value % CHAR_SET_SIZE));
        }
        return shortBuffer.toString();
    }

    public static String generateAppSecret(String appId, String appSalt) {
        return md5WithSalt(appId, appSalt, DEFAULT_MD5_ITERATIONS);
    }

    public static String generateAppSecret(String appId, String appSalt, String algorithm) {
        // ✅ 修复：用 Set.contains 替代 StringUtils.isAnyOf
        if (algorithm == null || !SUPPORTED_ALGORITHMS.contains(algorithm)) {
            log.warn("Unsupported algorithm: {}, fallback to MD5", algorithm);
            algorithm = "MD5";
        }
        return hashWithSalt(appId, appSalt, algorithm, DEFAULT_MD5_ITERATIONS);
    }

    public static String md5WithSalt(String password, String salt, int iterations) {
        return hashWithSalt(password, salt, "MD5", iterations);
    }

    private static String hashWithSalt(String content, String salt, String algorithm, int iterations) {
        // ✅ 保留：isAnyBlank 是 Commons Lang3 真实存在的方法
        if (StringUtils.isAnyBlank(content, salt)) {
            throw new IllegalArgumentException("Content and salt must not be blank");
        }

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] input = (content + salt).getBytes(StandardCharsets.UTF_8);

            byte[] hash = input;
            for (int i = 0; i < iterations; i++) {
                md.reset();
                md.update(hash);
                hash = md.digest();
            }

            return bytesToHex(hash);

        } catch (NoSuchAlgorithmException e) {
            log.error("Algorithm {} not supported in current JVM", algorithm, e);
            throw new IllegalStateException("Cryptographic algorithm error: " + algorithm, e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            hex.append(String.format("%02x", b & 0xFF));
        }
        return hex.toString();
    }

    public static String sha1SortedSign(String... params) {
        if (params == null || params.length == 0) {
            return "";
        }
        try {
            Arrays.sort(params);
            StringBuilder sb = new StringBuilder();
            for (String param : params) {
                if (param != null) {
                    sb.append(param);
                }
            }
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            byte[] digest = sha1.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-1 algorithm not available", e);
            throw new IllegalStateException("SHA-1 not supported", e);
        }
    }

    public static void main(String[] args) {
        String appSalt = "BLUC20G19J";
        String appId = generateAppKey();
        String appSecret = generateAppSecret(appId,appSalt);
        System.out.println("appId: "+appId);
        System.out.println("appKey: "+appSecret);

    }


}
