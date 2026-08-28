package com.jycloud.service.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMaProperties {

    /**
     * 多个小程序配置，通过 appId 区分
     */
    private List<AppConfig> apps;

    @Data
    public static class AppConfig {
        /**
         * 小程序 appId（必填，用于路由匹配）
         */
        private String appId;

        /**
         * 小程序 appSecret（必填）
         */
        private String secret;

        /**
         * 小程序消息 token（非必填）
         */
        private String token;

        /**
         * 小程序消息 aesKey（非必填）
         */
        private String aesKey;
    }
}
