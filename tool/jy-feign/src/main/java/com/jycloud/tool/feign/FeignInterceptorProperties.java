package com.jycloud.tool.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "jy.feign")
public class FeignInterceptorProperties {
    /** 允许透传的 Header 白名单（默认仅业务必要字段） */
    private List<String> allowedHeaders = List.of(
            "X-Tenant-ID",
            "X-Trace-Id",
            "X-Request-Id",
            "X-Data-Scope"  // 房票业务：数据权限
    );

    /** Token 头名称（默认兼容旧系统） */
    private String tokenHeader = "Authorization";
}