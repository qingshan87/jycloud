package com.jycloud.api.log.aspect;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.annotation.ApiLog;
import com.jycloud.api.log.constant.LogTypeEnum;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.event.ApiLogEvent;
import com.jycloud.api.log.utils.SysLogUtil;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.StringPool;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.common.util.IpUtil;
import com.jycloud.tool.common.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * AOP 记录 API 日志（拦截 {@code @ApiLog} 注解方法）——
 * 由 {@code LogClientAutoConfiguration} 注册为 Bean，无需组件扫描。
 */
@Slf4j
@Aspect
public class ApiLogAspect {

    private static final ThreadLocal<SysApiLog> THREAD_LOCAL = new ThreadLocal<>();

    @Pointcut("@annotation(com.jycloud.api.log.annotation.ApiLog)")
    public void pointcut() {
        // do nothing
    }

    /**
     * 返回通知
     *
     * @param ret       返回值
     * @param joinPoint 端点
     */
    @AfterReturning(returning = "ret", pointcut = "pointcut()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch(r -> {
            String s = JSON.toJSONString(ret);
            ServerResponse response = DtoEntityUtil.trans(ret, ServerResponse.class);
            SysApiLog apiLog = get();
            if (response == null) {
                apiLog.setType(LogTypeEnum.SUCCESS.getCode());
                apiLog.setResponseData(StringUtils.substring(String.valueOf(ret == null ? StringPool.EMPTY : ret), 0, SysLogUtil.MAX_LENGTH));
            } else {
                if (response.getCode() == ResponseCode.SUCCESS.getCode()) {
                    apiLog.setType(LogTypeEnum.SUCCESS.getCode());
                } else {
                    apiLog.setType(LogTypeEnum.FAIL.getCode());
                    apiLog.setExDetail(response.getMsg());
                }
                apiLog.setResponseCode(response.getCode());
                apiLog.setResponseMsg(response.getMsg());
                apiLog.setResponseData(StringUtils.substring(s, 0, SysLogUtil.MAX_LENGTH));
            }

            log.info("doAfterReturning : {}", JSON.toJSONString(apiLog));
            publishEvent(apiLog);
        });

    }

    /**
     * 异常通知
     *
     * @param joinPoint 端点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "pointcut()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        tryCatch(d -> {
            SysApiLog apiLog = get();
            apiLog.setType(LogTypeEnum.FAIL.getCode());
            apiLog.setResponseMsg(StringUtils.substring(e.getMessage(), 0, 1000));
            // 异常对象
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            apiLog.setExDetail(StringUtils.substring(stringWriter.toString(), 0, SysLogUtil.MAX_LENGTH));

            log.info("doAfterThrowable : {}", JSON.toJSONString(apiLog));
            publishEvent(apiLog);
        });
    }

    /**
     * 执行方法之前
     *
     * @param joinPoint 端点
     */
    @Before(value = "pointcut()")
    public void doBefore(JoinPoint joinPoint) {
        tryCatch(val -> {
            SysApiLog apiLog = null;
            try {
                apiLog = this.createApiLog(joinPoint);
            } catch (Exception e) {
                log.warn("组装 API 日志异常", e);
            }

            log.info("doBefore : {}", JSON.toJSONString(apiLog));
            THREAD_LOCAL.set(apiLog);
        });
    }

    private SysApiLog get() {
        SysApiLog apiLog = THREAD_LOCAL.get();
        if (apiLog == null) {
            return new SysApiLog();
        }
        return apiLog;
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {

            log.warn("记录API日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

    private void publishEvent(SysApiLog apiLog) {
        // 计算消耗时间,单位毫秒
        if (apiLog.getStartTime() != null) {
            Duration duration = Duration.between(apiLog.getStartTime(), LocalDateTime.now());

            apiLog.setCostTime(duration.toMillis());
        }
        apiLog.setFinishTime(LocalDateTime.now());
        apiLog.setCreateTime(LocalDateTime.now());
        SpringContextUtil.publishEvent(new ApiLogEvent(apiLog));
        THREAD_LOCAL.remove();
    }

    private SysApiLog createApiLog(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 注解上的描述
        ApiLog logAnnotation = method.getAnnotation(ApiLog.class);
        // 请求的类名
        String className = point.getTarget().getClass().getName();
        // 请求参数
        Object[] args = point.getArgs();
        String params = Arrays.stream(args)
                .map(arg -> JSON.toJSONString(arg))
                .collect(Collectors.joining(StringPool.COMMA));
        // 租戶 唯一请求标识 发起请求的应用
        String tenantId = UserContext.getTenantId();
        String requestId = UserContext.getTraceId();
        String clientId = UserContext.getClientId();

        HttpServletRequest request = currentRequest();

        SysApiLog apiLog = new SysApiLog();
        apiLog.setRequestId(requestId); //系统生成请求唯一ID
        apiLog.setClientId(clientId); //请求头获取
        apiLog.setTenantId(tenantId); //请求头获取
        apiLog.setServiceName(logAnnotation.serviceName()); //注解 api 服务id
        String ipAddr = IpUtil.getClientIp(request);
        if (StringUtils.isNotBlank(ipAddr)) {
            String[] ips = ipAddr.split(StringPool.COMMA);
            apiLog.setIp(ips[0]); //请求ip
        }
        apiLog.setActionId(logAnnotation.actionId()); //注解名称
        apiLog.setDescription(logAnnotation.description()); //注解
        apiLog.setClassPath(className);
        apiLog.setActionMethod(signature.getName());
        apiLog.setRequestUri(request == null ? null : request.getRequestURI());
        apiLog.setHttpMethod(request == null ? null : request.getMethod());
        apiLog.setParams(params);
        apiLog.setStartTime(LocalDateTime.now());

        return apiLog;
    }

    /**
     * 获取当前 Servlet 请求（非请求线程返回 {@code null}）。
     */
    private static HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes servletAttributes ? servletAttributes.getRequest() : null;
    }

}
