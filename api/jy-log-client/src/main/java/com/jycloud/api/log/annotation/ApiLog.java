package com.jycloud.api.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLog {
    /**
     * API 编号ID
     * @return
     */
    String actionId();

    /**
     * 提供API服务的服务名称
     */
    String serviceName();

    /**
     * API 功能描述
     * @return
     */
    String description() default"API描述";
}
