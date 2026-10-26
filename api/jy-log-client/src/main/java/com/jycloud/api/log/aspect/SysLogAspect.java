package com.jycloud.api.log.aspect;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.constant.LogTypeEnum;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.api.log.event.SysLogEvent;
import com.jycloud.api.log.utils.SysLogUtil;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.StringPool;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.common.util.SpringContextUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * AOP 记录系统操作日志（拦截 {@code @Log} 注解方法）——
 * 由 {@code LogClientAutoConfiguration} 注册为 Bean，无需组件扫描。
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class SysLogAspect {

    private static final ThreadLocal<SysLog> THREAD_LOCAL = new ThreadLocal<>();

    private final SysLogUtil sysLogUtil;

    @Pointcut("@annotation(com.jycloud.api.log.annotation.Log)")
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
            SysLog sysLog = get(joinPoint);
            if (response == null) {
                sysLog.setType(LogTypeEnum.SUCCESS.getCode());
                sysLog.setResult(StringUtils.substring(String.valueOf(ret == null ? StringPool.EMPTY : ret), 0, SysLogUtil.MAX_LENGTH));
            } else {
                if (response.getCode() == ResponseCode.SUCCESS.getCode()) {
                    sysLog.setType(LogTypeEnum.SUCCESS.getCode());
                } else {
                    sysLog.setType(LogTypeEnum.FAIL.getCode());
                    sysLog.setExDetail(response.getMsg());
                }
                sysLog.setResult(StringUtils.substring(s, 0, SysLogUtil.MAX_LENGTH));
            }

            log.info("doAfterReturning : {}", JSON.toJSONString(sysLog));
            publishEvent(sysLog);
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
            SysLog sysLog = get(joinPoint);
            sysLog.setType(LogTypeEnum.FAIL.getCode());
            sysLog.setResult(StringUtils.substring(e.getMessage(), 0, SysLogUtil.MAX_LENGTH));
            // 异常对象
            StringWriter stringWriter = new StringWriter();
            e.printStackTrace(new PrintWriter(stringWriter));
            sysLog.setExDetail(StringUtils.substring(stringWriter.toString(), 0, SysLogUtil.MAX_LENGTH));

            log.info("doAfterThrowable : {}", JSON.toJSONString(sysLog));
            publishEvent(sysLog);
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
            SysLog sysLog = awaitCreateLog(joinPoint);

            log.info("doBefore : {}", JSON.toJSONString(sysLog));

            THREAD_LOCAL.set(sysLog);
        });
    }

    /**
     * 优先取前置通知缓存的日志实体；缺失（前置通知异常/已被清理）时按当前连接点重建。
     */
    private SysLog get(JoinPoint joinPoint) {
        SysLog sysLog = THREAD_LOCAL.get();
        if (sysLog == null) {
            log.warn("THREAD_LOCAL 中未找到系统操作日志实体，按当前连接点重建");
            sysLog = awaitCreateLog(joinPoint);
        }
        return sysLog;
    }

    private SysLog awaitCreateLog(JoinPoint joinPoint) {
        try {
            return sysLogUtil.createLog(joinPoint);
        } catch (Exception e) {
            log.warn("组装系统操作日志异常", e);
            return new SysLog();
        }
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录操作日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

    private void publishEvent(SysLog sysLog) {
        // 计算消耗时间,单位毫秒
        if (sysLog.getStartTime() != null) {
            Duration duration = Duration.between(sysLog.getStartTime(), LocalDateTime.now());

            sysLog.setConsumingTime(duration.toMillis());
        }
        sysLog.setFinishTime(LocalDateTime.now());
        sysLog.setCreateTime(LocalDateTime.now());

        SpringContextUtil.publishEvent(new SysLogEvent(sysLog));
        THREAD_LOCAL.remove();
    }

}
