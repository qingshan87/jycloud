package com.jycloud.tool.common.util;

import com.jycloud.tool.common.constant.StringPool;
import com.jycloud.tool.common.properties.Ip2RegionProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * IP 地址查询工具类（ip2region 2.7.0 专用版）
 */
@Slf4j
@Component
public class AddressUtil {

    /**
     * 缓存策略（2.7.0 仅支持这三种，无 Version 参数）
     */
    public enum CachePolicy {
        FILE_ONLY,           // 基于文件，每次查询有磁盘 IO
        VECTOR_INDEX,        // 缓存 512KB 向量索引，推荐
        FULL_CONTENT         // 全内存缓存，查询最快但占用 ~10MB
    }

    private Searcher searcher;
    private final Ip2RegionProperties ip2RegionProperties;
    private final CachePolicy cachePolicy;

    public AddressUtil(Ip2RegionProperties ip2RegionProperties) {
        this.ip2RegionProperties = ip2RegionProperties;
        this.cachePolicy = CachePolicy.VECTOR_INDEX;
    }

    @PostConstruct
    public void init() {
        try {
            String dbPath = resolveDbPath(ip2RegionProperties.getPath());
            if (StringUtils.isBlank(dbPath)) {
                log.error("ip2region db path is empty");
                return;
            }

            // 文件预检（无 verifyFromFile，手动检查）
            File dbFile = new File(dbPath);
            if (!dbFile.exists() || !dbFile.canRead() || dbFile.length() == 0) {
                log.error("ip2region db file invalid: {}", dbPath);
                return;
            }

            // 创建 Searcher（注意：方法签名无 Version 参数！）
            this.searcher = createSearcher270(dbPath, cachePolicy);
            log.info("ip2region 2.7.0 initialized, policy={}, path={}, size={}KB",
                    cachePolicy, dbPath, dbFile.length() / 1024);

        } catch (IOException e) {
            log.error("init ip2region failed", e);
            throw new RuntimeException("Failed to initialize ip2region", e);
        }
    }

    /**
     * 2.7.0 专用：创建 Searcher（无 Version 参数）
     */
    private Searcher createSearcher270(String dbPath, CachePolicy policy) throws IOException {
        return switch (policy) {
            case FILE_ONLY ->
                // 签名：newWithFileOnly(String dbPath)
                    Searcher.newWithFileOnly(dbPath);

            case VECTOR_INDEX -> {
                // 签名：loadVectorIndexFromFile(String) + newWithVectorIndex(String, byte[])
                byte[] vIndex = Searcher.loadVectorIndexFromFile(dbPath);
                yield Searcher.newWithVectorIndex(dbPath, vIndex);
            }

            case FULL_CONTENT -> {
                // 签名：loadContentFromFile(String) + newWithBuffer(byte[])
                byte[] cBuff = Searcher.loadContentFromFile(dbPath);
                yield Searcher.newWithBuffer(cBuff);
            }
        };
    }

    private String resolveDbPath(String path) throws IOException {
        if (StringUtils.isBlank(path)) return null;
        if (path.startsWith("classpath:")) {
            String resourcePath = path.substring("classpath:".length());
            ClassPathResource resource = new ClassPathResource(resourcePath);
            return resource.exists() ? resource.getFile().getAbsolutePath() : null;
        }
        File file = new File(path);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    @PreDestroy
    public void destroy() {
        if (searcher != null) {
            try {
                searcher.close();
                log.info("ip2region searcher closed");
            } catch (IOException e) {
                log.warn("close searcher error", e);
            }
        }
    }

    /**
     * 查询 IP 地域（2.7.0 直接 search，无 Version 区分）
     */
    public String getRegion(String ip) {
        if (searcher == null || StringUtils.isBlank(ip)) {
            return StringPool.EMPTY;
        }
        try {
            String result = searcher.search(ip);
            return result != null ? result : StringPool.EMPTY;
        } catch (Exception e) {
            log.error("query ip region failed, ip={}", ip, e);
            return StringPool.EMPTY;
        }
    }

    // // ✅ 静态兼容方法（过渡用）
    // @Deprecated
    // public static String getRegionStatic(String ip) {
    //     AddressUtil instance = SpringContextUtil.getBean(AddressUtil.class);
    //     return instance != null ? instance.getRegion(ip) : StringPool.EMPTY;
    // }
}