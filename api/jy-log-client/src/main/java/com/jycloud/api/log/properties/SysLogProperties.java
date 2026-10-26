package com.jycloud.api.log.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 日志模块配置项，前缀 {@code sys.log}。
 *
 * <p>经 {@code LogClientAutoConfiguration} 的 {@code @EnableConfigurationProperties}
 * 注册，不再组件扫描；{@code applicationName} 取自 {@code spring.application.name}，
 * 用于日志的 serviceId / serviceName 字段。</p>
 *
 * @author Lqs
 * @date 2022/7/27 17:45
 */
@Data
@ConfigurationProperties(prefix = "sys.log")
public class SysLogProperties {

    /**
     * 应用名称（日志来源服务标识），默认取 {@code spring.application.name}。
     */
    @Value("${spring.application.name:unknown}")
    private String applicationName;

    /**
     * 是否记录日志（当前用于错误日志事件的开关），默认关闭。
     */
    private Boolean enabled = false;
}
