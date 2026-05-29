package com.jycloud.tool.common.util;

import com.fasterxml.uuid.Generators;
import lombok.experimental.UtilityClass;
import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;


@UtilityClass
public class UUIDv7Util {

    // ✅ 全局单例（线程安全，启动时仅探测一次网卡/MAC）
    private static final TimeBasedEpochGenerator GENERATOR = Generators.timeBasedEpochGenerator();

    /**
     * 生成 32 位无横杠 UUID v7（推荐传输/存储格式）
     */
    public static String generate() {
        return GENERATOR.generate().toString().replace("-", "");
    }

    /**
     * 生成标准 36 位带横杠 UUID v7（符合 RFC 格式）
     */
    public static String generateFormatted() {
        return GENERATOR.generate().toString();
    }
}
