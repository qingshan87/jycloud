package com.jycloud.tool.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(feign.Feign.class)
@EnableConfigurationProperties(FeignInterceptorProperties.class)
public class FeignAutoConfiguration {

    @Bean
    public FeignHeaderInterceptor feignHeaderInterceptor(FeignInterceptorProperties props) {
        return new FeignHeaderInterceptor(props.getAllowedHeaders(), props.getTokenHeader());
    }
}