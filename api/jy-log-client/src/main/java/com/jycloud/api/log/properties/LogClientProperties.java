package com.jycloud.api.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * 日志客户端配置项，前缀 {@code jy.log.client} —— 控制日志写入服务（jy-user）
 * 的基础地址与连接/读取超时。
 *
 * <p>默认按 Nacos 服务名经负载均衡调用；如联调/迁移期需要指向其他注册名或域名，
 * 调整 {@code jy.log.client.base-url} 即可。</p>
 *
 * @author jycloud
 * @since 2026-09
 */
@Data
@ConfigurationProperties(prefix = "jy.log.client")
public class LogClientProperties {

    /**
     * 日志写入服务基础地址，默认 {@code http://jy-user}（Nacos 注册名，经 LoadBalancer 解析实例）。
     */
    private String baseUrl = "http://jy-user";

    /**
     * 连接超时，默认 5 秒。
     */
    private Duration connectTimeout = Duration.ofSeconds(5);

    /**
     * 读取超时，默认 30 秒。
     */
    private Duration readTimeout = Duration.ofSeconds(30);

}
