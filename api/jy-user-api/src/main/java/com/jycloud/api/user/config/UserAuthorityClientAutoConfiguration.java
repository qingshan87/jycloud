package com.jycloud.api.user.config;

import com.jycloud.api.user.client.UserAuthorityClient;
import com.jycloud.tool.http.UserContextRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;

/**
 * 基于 Spring HttpExchange 的用户权限服务客户端自动配置。
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({RestClient.class, LoadBalancerClient.class, UserContextRequestInterceptor.class})
@EnableConfigurationProperties(UserAuthorityClientProperties.class)
public class UserAuthorityClientAutoConfiguration {

    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean(name = "userAuthorityRestClientBuilder")
    public RestClient.Builder userAuthorityRestClientBuilder(
            UserAuthorityClientProperties properties,
            UserContextRequestInterceptor requestInterceptor) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(createRequestFactory(properties))
                .requestInterceptor(requestInterceptor);
    }

    @Bean
    @ConditionalOnMissingBean(name = "userAuthorityRestClient")
    public RestClient userAuthorityRestClient(
            @Qualifier("userAuthorityRestClientBuilder") RestClient.Builder builder) {
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "userAuthorityHttpServiceProxyFactory")
    public HttpServiceProxyFactory userAuthorityHttpServiceProxyFactory(
            @Qualifier("userAuthorityRestClient") RestClient restClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserAuthorityClient userAuthorityClient(
            @Qualifier("userAuthorityHttpServiceProxyFactory") HttpServiceProxyFactory proxyFactory) {
        return proxyFactory.createClient(UserAuthorityClient.class);
    }

    private static ClientHttpRequestFactory createRequestFactory(UserAuthorityClientProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());
        return requestFactory;
    }
}
