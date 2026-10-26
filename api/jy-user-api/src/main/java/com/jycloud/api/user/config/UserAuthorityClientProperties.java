package com.jycloud.api.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 用户权限 HTTP 服务客户端配置。
 */
@Data
@ConfigurationProperties(prefix = "jy.user-authority.client")
public class UserAuthorityClientProperties {

    /**
     * jy-user 服务地址，默认使用 Nacos 注册名经 Spring Cloud LoadBalancer 解析。
     */
    private String baseUrl = "http://jy-user";

    private Duration connectTimeout = Duration.ofSeconds(5);

    private Duration readTimeout = Duration.ofSeconds(30);
}
