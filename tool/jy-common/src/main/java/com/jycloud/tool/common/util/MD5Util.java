package com.jycloud.tool.common.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MD5Util {

    // 工具方法：将字节数组转为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 工具方法：生成一个随机盐值（十六进制字符串，长度16，即8字节）
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * 使用指定的盐值进行MD5加密
     * @param password 明文密码
     * @param saltHex 十六进制表示的盐值（由调用者提供）
     * @return 计算出的MD5哈希值（十六进制字符串）
     */
    public static String encryptWithSalt(String password, String saltHex) {
        try {
            // 密码与盐值拼接（顺序可自定义）
            String passwordWithSalt = password + saltHex;
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(passwordWithSalt.getBytes());
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    /**
     * 验证密码
     * @param inputPassword 用户输入的明文密码
     * @param saltHex 存储的盐值（十六进制）
     * @param storedHash 数据库中存储的哈希值
     * @return 是否匹配
     */
    public static boolean verifyWithSalt(String inputPassword, String saltHex, String storedHash) {
        String computedHash = encryptWithSalt(inputPassword, saltHex);
        // 使用安全比较
        return MessageDigest.isEqual(computedHash.getBytes(), storedHash.getBytes());
    }

    // ========== 使用示例 ==========
    public static void main(String[] args) {
        // 1. 生成盐值（也可从外部配置读取）
        // String salt = generateSalt();
        // System.out.println("生成的盐值: " + salt);

        String salt = "zhangsan";
        String password = "mySecret123";

        // 2. 使用该盐值加密
        // salt = null;
        String hash = encryptWithSalt(password, salt);
        System.out.println("哈希值: " + hash);
        //269b4ce6a001d5ef6e3e5fdf57e66787
        //f1acb24a23f14045375808409f793711

        // 3. 存储到数据库时，同时保存 salt 和 hash（可拼接，也可分字段）
        String storedSalt = salt;
        String storedHash = hash;

        // 4. 验证：用户输入密码，从数据库取出 sa和 hash
        boolean valid = verifyWithSalt("mySecret123", storedSalt, storedHash);
        System.out.println("正确密码验证: " + valid); // true

        boolean invalid = verifyWithSalt("wrongPwd", storedSalt, storedHash);
        System.out.println("错误密码验证: " + invalid); // false

        String encryptToken = JWTUtil.encryptToken(JWTUtil.createToken("admin", "1", "000000"));
        System.out.println("加密token: " + encryptToken);

        salt = generateSalt();
        System.out.println("生成的盐值: " + salt);
        System.out.println(encryptWithSalt("admin123", salt));

    }
}