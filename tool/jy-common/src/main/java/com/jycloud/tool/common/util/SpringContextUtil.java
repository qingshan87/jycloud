package com.jycloud.tool.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Spring Context 工具类
 * 用于在非 Spring 管理的类中安全获取 Bean 或发布事件
 *
 * @author qingshan
 * @update 2026/05/25 - 适配生产环境规范 + Spring Boot 3.2
 */
@Slf4j
@Component
public class SpringContextUtil implements ApplicationContextAware {

    // ✅ volatile 保证多线程可见性（Spring 启动期单线程赋值，但严谨起见保留）
    private static volatile ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextUtil.applicationContext = context;
    }

    /**
     * 获取 Bean（按类型）
     */
    @Nullable
    public static <T> T getBean(Class<T> clazz) {
        checkContext();
        return applicationContext.getBean(clazz);
    }

    /**
     * 获取 Bean（按名称）
     */
    @Nullable
    public static Object getBean(String name) {
        checkContext();
        return applicationContext.getBean(name);
    }

    /**
     * 获取 Bean（按名称+类型）
     */
    @Nullable
    public static <T> T getBean(String name, Class<T> requiredType) {
        checkContext();
        return applicationContext.getBean(name, requiredType);
    }

    public static boolean containsBean(String name) {
        return applicationContext != null && applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        checkContext();
        return applicationContext.isSingleton(name);
    }

    @Nullable
    public static Class<?> getType(String name) {
        checkContext();
        return applicationContext.getType(name);
    }

    @Nullable
    public static ApplicationContext getContext() {
        return applicationContext;
    }

    /**
     * 发布 Spring 事件
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            log.warn("Cannot publish event: Spring ApplicationContext not initialized yet.");
            return;
        }
        try {
            applicationContext.publishEvent(event);
        } catch (Exception e) {
            // ✅ 替换 printStackTrace 为日志记录，生产可追踪
            log.error("Failed to publish Spring event: {}", event.getClass().getSimpleName(), e);
        }
    }

    /**
     * 统一上下文检查（避免重复判空）
     */
    private static void checkContext() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "Spring ApplicationContext is not initialized yet. " +
                            "Ensure SpringContextUtil is scanned and called after context startup."
            );
        }
    }
}