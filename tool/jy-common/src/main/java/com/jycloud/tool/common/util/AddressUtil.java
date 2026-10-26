package com.jycloud.tool.common.util;

import com.jycloud.tool.common.constant.StringPool;
import com.jycloud.tool.common.properties.Ip2RegionProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.service.Config;
import org.lionsoul.ip2region.service.ConfigBuilder;
import org.lionsoul.ip2region.service.Ip2Region;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * IP 地址查询工具类（基于 ip2region 3.3.x，当前启用 IPv4）。
 */
@Slf4j
@Component
public class AddressUtil {

    private static final String CLASSPATH_PREFIX = "classpath:";

    private volatile Ip2Region ip2Region;

    private final Ip2RegionProperties ip2RegionProperties;

    public AddressUtil(Ip2RegionProperties ip2RegionProperties) {
        this.ip2RegionProperties = ip2RegionProperties;
    }

    @PostConstruct
    public void init() {
        String path = ip2RegionProperties.getPath();
        if (StringUtils.isBlank(path)) {
            log.warn("sys.ip2region.path is empty, IP region query disabled");
            return;
        }
        try {
            this.ip2Region = Ip2Region.create(buildV4Config(path), null);
            log.info("ip2region initialized, xdb={}", path);
        } catch (Exception e) {
            log.error("init ip2region failed, IP region query disabled, xdb={}", path, e);
        }
    }

    private Config buildV4Config(String path) throws Exception {
        ConfigBuilder builder = Config.custom()
                .setSearchers(Runtime.getRuntime().availableProcessors() * 2);

        if (path.startsWith(CLASSPATH_PREFIX)) {
            String location = path.substring(CLASSPATH_PREFIX.length());
            ClassPathResource resource = new ClassPathResource(location);
            if (!resource.exists()) {
                throw new FileNotFoundException("ip2region xdb not found in classpath: " + location);
            }
            try (InputStream inputStream = resource.getInputStream()) {
                return builder.setCachePolicy(Config.BufferCache)
                        .setXdbInputStream(inputStream)
                        .asV4();
            }
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("ip2region xdb not found: " + file.getAbsolutePath());
        }
        return builder.setCachePolicy(Config.VIndexCache)
                .setXdbFile(file)
                .asV4();
    }

    @PreDestroy
    public void destroy() {
        Ip2Region region = this.ip2Region;
        if (region != null) {
            try {
                region.close();
                log.info("ip2region closed");
            } catch (Exception e) {
                log.warn("close ip2region error", e);
            }
        }
    }

    public String getRegion(String ip) {
        if (StringUtils.isBlank(ip)) {
            return StringPool.EMPTY;
        }
        Ip2Region region = this.ip2Region;
        if (region == null) {
            return fallbackLocation(ip);
        }
        try {
            String result = region.search(ip);
            return formatRegion(ip, result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("query ip region interrupted, ip={}", ip);
            return fallbackLocation(ip);
        } catch (Exception e) {
            log.error("query ip region failed, ip={}", ip, e);
            return fallbackLocation(ip);
        }
    }

    /**
     * 将 ip2region 的原始管道分隔结果转换为可读地点。
     */
    static String formatRegion(String ip, String rawRegion) {
        if (StringUtils.isBlank(rawRegion)) {
            return fallbackLocation(ip);
        }
        String[] parts = rawRegion.split("\\|");
        if (isReservedRegion(parts)) {
            return fallbackLocation(ip);
        }

        List<String> locationParts = new ArrayList<>();
        for (String part : parts) {
            String value = StringUtils.trimToNull(part);
            if (value == null || "0".equals(value) || StringUtils.equalsIgnoreCase(value, "Reserved")) {
                continue;
            }
            if (locationParts.isEmpty() || !StringUtils.equalsIgnoreCase(locationParts.getLast(), value)) {
                locationParts.add(value);
            }
        }
        return locationParts.isEmpty() ? fallbackLocation(ip) : String.join(" ", locationParts);
    }

    private static boolean isReservedRegion(String[] parts) {
        for (String part : parts) {
            String value = StringUtils.trimToEmpty(part);
            if (StringUtils.isNotBlank(value) && !"0".equals(value)
                    && !StringUtils.equalsIgnoreCase(value, "Reserved")) {
                return false;
            }
        }
        return true;
    }

    private static String fallbackLocation(String ip) {
        return isPrivateAddress(ip) ? "内网地址" : "未知地点";
    }

    /**
     * 判断私网、回环、链路本地和常见保留地址。
     */
    private static boolean isPrivateAddress(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            if (address.isAnyLocalAddress() || address.isLoopbackAddress()
                    || address.isSiteLocalAddress() || address.isLinkLocalAddress()) {
                return true;
            }
            return ip.startsWith("100.") && isCarrierGradeNat(ip);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isCarrierGradeNat(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4 || !"100".equals(parts[0])) {
            return false;
        }
        try {
            int second = Integer.parseInt(parts[1]);
            return second >= 64 && second <= 127;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
