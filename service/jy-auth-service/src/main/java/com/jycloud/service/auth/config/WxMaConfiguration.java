package com.jycloud.service.auth.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WxMaProperties.class)
public class WxMaConfiguration {

    private final WxMaProperties wxMaProperties;

    /**
     * appId → WxMaService 映射
     */
    private final Map<String, WxMaService> maServices = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<WxMaProperties.AppConfig> apps = wxMaProperties.getApps();
        if (apps == null || apps.isEmpty()) {
            log.warn("未配置微信小程序，跳过 WxMaService 初始化");
            return;
        }
        maServices.putAll(apps.stream()
                .filter(app -> StringUtils.isNotBlank(app.getAppId()) && StringUtils.isNotBlank(app.getSecret()))
                .map(this::buildWxMaService)
                .collect(Collectors.toMap(
                        svc -> svc.getWxMaConfig().getAppid(),
                        svc -> svc,
                        (exist, replace) -> exist)));
        log.info("已初始化 {} 个微信小程序 WxMaService: {}", maServices.size(), maServices.keySet());
    }

    private WxMaService buildWxMaService(WxMaProperties.AppConfig appConfig) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(appConfig.getAppId());
        config.setSecret(appConfig.getSecret());
        if (StringUtils.isNotBlank(appConfig.getToken())) {
            config.setToken(appConfig.getToken());
        }
        if (StringUtils.isNotBlank(appConfig.getAesKey())) {
            config.setAesKey(appConfig.getAesKey());
        }

        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        log.info("WxMaService 初始化完成: appId={}", appConfig.getAppId());
        return service;
    }

    /**
     * 根据 appId 获取对应的 WxMaService
     *
     * @param appId 小程序 appId
     * @return WxMaService，未匹配则返回 null
     */
    public WxMaService getMaService(String appId) {
        return maServices.get(appId);
    }
}
