package com.jycloud.tool.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressUtilTest {

    @Test
    void shouldConvertReservedPrivateAddressToReadableLocation() {
        assertEquals("内网地址",
                AddressUtil.formatRegion("192.168.1.10", "Reserved|Reserved|Reserved|0|0"));
        assertEquals("内网地址",
                AddressUtil.formatRegion("127.0.0.1", "Reserved|Reserved|Reserved|0|0"));
    }

    @Test
    void shouldConvertReservedPublicAddressToUnknownLocation() {
        assertEquals("未知地点",
                AddressUtil.formatRegion("8.8.8.8", "Reserved|Reserved|Reserved|0|0"));
    }

    @Test
    void shouldFormatValidRegionAndRemovePlaceholders() {
        assertEquals("中国 浙江省 杭州市 电信",
                AddressUtil.formatRegion("120.202.209.221", "中国|0|浙江省|杭州市|电信"));
    }
}
