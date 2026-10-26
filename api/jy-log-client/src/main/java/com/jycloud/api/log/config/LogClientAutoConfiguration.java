package com.jycloud.api.log.config;

import com.jycloud.api.log.aspect.ApiLogAspect;
import com.jycloud.api.log.aspect.SysLogAspect;
import com.jycloud.api.log.client.ILogClient;
import com.jycloud.api.log.client.fallback.LogClientFallback;
import com.jycloud.api.log.event.listener.ApiLogListener;
import com.jycloud.api.log.event.listener.LogErrorListener;
import com.jycloud.api.log.event.listener.SysLogListener;
import com.jycloud.api.log.handler.LogExceptionHandler;
import com.jycloud.api.log.properties.LogClientProperties;
import com.jycloud.api.log.properties.SysLogProperties;
import com.jycloud.api.log.utils.SysLogUtil;
import com.jycloud.tool.http.UserContextRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.net.http.HttpClient;

/**
 * 日志客户端自动配置 —— 覆盖 jy-user（用户服务，日志实现所在服务）日志写入链路，
 * 经 META-INF/spring/...AutoConfiguration.imports 注册，业务模块引入 jy-log-client 后
 * 无需组件扫描即可获得完整日志能力：
 *
 * <ul>
 *     <li>{@link ILogClient}：{@code @HttpExchange} 接口经 {@link HttpServiceProxyFactory}
 *         生成动态代理，等价于原 Feign 客户端（服务名 {@code jy-user}，路径 {@code /client/log/**}）；</li>
 *     <li>{@link SysLogListener} / {@link LogErrorListener} / {@link ApiLogListener}：
 *         监听日志事件并把日志实体经 {@link LogClientFallback} 兜底写入 jy-user；</li>
 *     <li>{@link SysLogAspect} / {@link ApiLogAspect}：{@code @Log} / {@code @ApiLog}
 *         注解切面，在业务方法执行前后组装日志并发布事件；</li>
 *     <li>{@link MyErrorAttributes} / {@link LogExceptionHandler}（仅 Servlet Web 应用）：
 *         定制全局错误响应并发布服务异常事件。</li>
 * </ul>
 *
 * <p>连接/读取超时由 {@link LogClientProperties}（{@code jy.log.client.*}）控制，底层使用
 * JDK {@link HttpClient}；出站请求经 {@link UserContextRequestInterceptor}（jy-http-client 公共组件）
 * 全量透传入站请求头，下游 TokenHandler 可还原用户上下文。</p>
 *
 * <p>{@link AutoConfigureBefore @AutoConfigureBefore}：保证自定义 {@link MyErrorAttributes}
 * 先于 {@link ErrorMvcAutoConfiguration} 注册，使 Boot 默认 ErrorAttributes 正常退避
 * （不依赖类名排序的隐式约定）。</p>
 *
 * @author jycloud
 * @since 2026-09
 */
@AutoConfiguration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@ConditionalOnClass({ RestClient.class, LoadBalancerClient.class })
@EnableConfigurationProperties({ LogClientProperties.class, SysLogProperties.class })
public class LogClientAutoConfiguration {

    /**
     * 负载均衡 RestClient 构建器：@LoadBalanced 由 Spring Cloud 负责注入
     * DeferringLoadBalancerInterceptor，使 {@code http://jy-user/...} 按服务名解析实例；
     * 同时设置 baseUrl 与 JDK HttpClient 连接/读取超时。
     */
    @Bean
    @LoadBalanced
    @ConditionalOnMissingBean(name = "logClientRestClientBuilder")
    public RestClient.Builder logClientRestClientBuilder(LogClientProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(createRequestFactory(properties))
                .requestInterceptor(new UserContextRequestInterceptor());
    }

    @Bean
    @ConditionalOnMissingBean(name = "logClientRestClient")
    public RestClient logClientRestClient(
            @Qualifier("logClientRestClientBuilder") RestClient.Builder logClientRestClientBuilder) {
        return logClientRestClientBuilder.build();
    }

    /**
     * HTTP 接口代理工厂：基于带负载均衡的 RestClient 构建，供 {@link ILogClient} 代理 Bean 使用。
     */
    @Bean
    @ConditionalOnMissingBean(name = "logClientHttpServiceProxyFactory")
    public HttpServiceProxyFactory logClientHttpServiceProxyFactory(
            @Qualifier("logClientRestClient") RestClient logClientRestClient) {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(logClientRestClient)).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ILogClient iLogClient(
            @Qualifier("logClientHttpServiceProxyFactory") HttpServiceProxyFactory logClientHttpServiceProxyFactory) {
        return logClientHttpServiceProxyFactory.createClient(ILogClient.class);
    }

    /**
     * 日志工具：组装操作/错误日志实体并发布日志事件，供切面与异常处理器共用。
     */
    @Bean
    @ConditionalOnMissingBean
    public SysLogUtil sysLogUtil(SysLogProperties sysLogProperties) {
        return new SysLogUtil(sysLogProperties);
    }

    /**
     * 系统操作日志监听器：消费 {@code SysLogEvent} 并写入 jy-user。
     * 写入链路经 {@link LogClientFallback} 包裹，失败仅记录错误、不影响业务。
     */
    @Bean
    @ConditionalOnMissingBean
    public SysLogListener sysLogListener(ILogClient iLogClient) {
        return new SysLogListener(new LogClientFallback(iLogClient)::saveSysLog);
    }

    /**
     * 系统错误日志监听器：消费 {@code LogErrorEvent} 并写入 jy-user。
     */
    @Bean
    @ConditionalOnMissingBean
    public LogErrorListener logErrorListener(ILogClient iLogClient) {
        return new LogErrorListener(new LogClientFallback(iLogClient)::saveLogError);
    }

    /**
     * API 日志监听器：消费 {@code ApiLogEvent} 并写入 jy-user。
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiLogListener apiLogListener(ILogClient iLogClient) {
        return new ApiLogListener(new LogClientFallback(iLogClient)::saveApiLog);
    }

    /**
     * 操作日志切面：拦截 {@code @Log} 注解方法，记录操作日志。
     */
    @Bean
    @ConditionalOnMissingBean
    public SysLogAspect sysLogAspect(SysLogUtil sysLogUtil) {
        return new SysLogAspect(sysLogUtil);
    }

    /**
     * API 日志切面：拦截 {@code @ApiLog} 注解方法，记录 API 日志。
     */
    @Bean
    @ConditionalOnMissingBean
    public ApiLogAspect apiLogAspect() {
        return new ApiLogAspect();
    }

    /**
     * 基于 JDK HttpClient 的请求工厂：连接超时设置在 HttpClient 上，读取超时设置在工厂上。
     */
    private static ClientHttpRequestFactory createRequestFactory(LogClientProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.getConnectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(properties.getReadTimeout());
        return requestFactory;
    }

    /**
     * Servlet MVC 专属组件：以嵌套配置隔离 spring-webmvc 依赖，WebFlux 等非
     * Servlet 环境整类跳过（条件经 ASM 求值，不加载相关类型）。
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(DefaultErrorAttributes.class)
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    static class ServletLogConfiguration {

        /**
         * 全局错误响应定制：替换 Boot 默认 ErrorAttributes，统一 ServerResponse 结构，
         * 并发布服务异常事件（允许业务方以自定义 ErrorAttributes 覆盖）。
         */
        @Bean
        @ConditionalOnMissingBean(ErrorAttributes.class)
        public MyErrorAttributes myErrorAttributes(SysLogUtil sysLogUtil) {
            return new MyErrorAttributes(sysLogUtil);
        }

        /**
         * 全局异常处理器：在 jy-common 默认处理逻辑之上发布服务异常事件
         * （优先级 HIGHEST_PRECEDENCE + 1，先于默认处理器拦截）。
         */
        @Bean
        @ConditionalOnMissingBean
        public LogExceptionHandler logExceptionHandler(SysLogUtil sysLogUtil) {
            return new LogExceptionHandler(sysLogUtil);
        }
    }

}
