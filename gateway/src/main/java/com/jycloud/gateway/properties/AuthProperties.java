package com.jycloud.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 网关认证配置。
 */
@Data
@Component
@ConfigurationProperties(prefix = "sys.auth")
public class AuthProperties {

    private boolean enabled = true;

    private List<String> whiteList = List.of(
            "/jy-user/system/auth/login",
            "/jy-user/system/auth/register",
            "/error",
            "/favicon.ico");
}
