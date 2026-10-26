package com.jycloud.tool.http;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * RestClient 请求头透传自动配置。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({ClientHttpRequestInterceptor.class, HttpServletRequest.class})
public class HttpClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UserContextRequestInterceptor userContextRequestInterceptor() {
        return new UserContextRequestInterceptor();
    }
}
