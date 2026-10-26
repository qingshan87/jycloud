package com.jycloud.api.log.utils;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.annotation.Log;
import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.api.log.event.LogErrorEvent;
import com.jycloud.api.log.properties.SysLogProperties;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.util.IpUtil;
import com.jycloud.tool.common.util.SpringContextUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志工具（Spring Bean，经 {@code LogClientAutoConfiguration} 注册）——
 * 组装操作日志 / 错误日志实体并发布对应事件，供切面与异常处理器共用。
 *
 * <p>原实现以静态字段在类加载期从 Spring 上下文抓取 {@link SysLogProperties}，
 * 存在初始化时序隐患；现改为构造注入，随自动配置生命周期装配。</p>
 *
 * @author Lqs
 * @date 2022/7/27 14:44
 */
@Slf4j
public class SysLogUtil {

    /**
     * 日志字段最大长度（对应数据库 TEXT 上限），超出部分截断。
     */
    public static final int MAX_LENGTH = 65535;

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    private final SysLogProperties sysLogProperties;

    public SysLogUtil(SysLogProperties sysLogProperties) {
        this.sysLogProperties = sysLogProperties;
    }

    /**
     * 发布服务异常事件（{@code sys.log.enabled=false} 时跳过）。
     *
     * @param error 异常对象
     */
    public void publishLogErrorEvent(Throwable error) {
        if (!Boolean.TRUE.equals(sysLogProperties.getEnabled())) {
            return;
        }
        try {
            LogError logError = createLogError(error);
            SpringContextUtil.publishEvent(new LogErrorEvent(logError));
        } catch (Exception e) {
            // 错误日志属旁路行为：组装/发布失败仅记录，不得影响业务主流程与错误响应
            log.error("发布服务异常事件失败", e);
        }
    }

    /**
     * 依据连接点组装操作日志实体（含请求信息、参数、用户上下文）。
     *
     * @param point 连接点
     * @return 操作日志实体
     */
    public SysLog createLog(JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 注解上的描述
        Log logAnnotation = method.getAnnotation(Log.class);
        // 请求的类名
        String className = point.getTarget().getClass().getName();
        // 请求参数（-parameters 编译参数已开启，可解析真实形参名）
        String[] paramNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        Object[] args = point.getArgs();
        HttpServletRequest request = currentRequest();
        Map<String, Object> paramsMap = handleParams(request, args, paramNames);
        String ua = request == null ? null
                : StringUtils.substring(request.getHeader(HttpHeaders.USER_AGENT), 0, 500);

        SysLog sysLog = new SysLog();
        sysLog.setTenantId(UserContext.getTenantId());
        sysLog.setServiceId(sysLogProperties.getApplicationName());
        sysLog.setUsername(UserContext.getUserName());
        sysLog.setIp(IpUtil.getClientIp(request));
        sysLog.setDescription(StringUtils.substring(logAnnotation == null ? null : logAnnotation.value(), 0, 500));
        sysLog.setClassPath(className);
        sysLog.setActionMethod(signature.getName());
        sysLog.setRequestUri(request == null ? null : request.getRequestURI());
        sysLog.setHttpMethod(request == null ? null : request.getMethod());
        sysLog.setParams(StringUtils.substring(JSON.toJSONString(paramsMap), 0, MAX_LENGTH));
        sysLog.setStartTime(LocalDateTime.now());
        sysLog.setUa(ua);
        sysLog.setCreateUid(parseUserId(UserContext.getUserId()));
        sysLog.setCreateTime(LocalDateTime.now());

        log.info("sysLog: {}", sysLog);
        return sysLog;
    }

    /**
     * 组装错误日志实体（含异常堆栈、请求信息、用户上下文）。
     */
    private LogError createLogError(Throwable error) {
        LogError logError = new LogError();
        if (error == null) {
            return logError;
        }
        if (StringUtils.isNotBlank(error.getMessage())) {
            logError.setDescription(StringUtils.substring(error.getMessage(), 0, 500));
        }

        StringWriter stringWriter = new StringWriter();
        error.printStackTrace(new PrintWriter(stringWriter));
        logError.setExDetail(StringUtils.substring(stringWriter.toString(), 0, MAX_LENGTH));

        StackTraceElement[] elements = error.getStackTrace();
        if (ObjectUtils.isNotEmpty(elements)) {
            StackTraceElement element = elements[0];
            logError.setClassPath(element.getClassName());
            logError.setActionMethod(element.getMethodName());
        }

        HttpServletRequest request = currentRequest();
        logError.setParams(request == null ? null
                : StringUtils.substring(request.getQueryString(), 0, MAX_LENGTH));
        logError.setServiceId(sysLogProperties.getApplicationName());
        logError.setIp(IpUtil.getClientIp(request));
        logError.setHttpMethod(request == null ? null : request.getMethod());
        logError.setRequestUri(request == null ? null : request.getRequestURI());
        logError.setUa(request == null ? null
                : StringUtils.substring(request.getHeader(HttpHeaders.USER_AGENT), 0, 500));
        logError.setUsername(UserContext.getUserName());
        logError.setTenantId(UserContext.getTenantId());
        logError.setCreateUid(parseUserId(UserContext.getUserId()));
        logError.setCreateTime(LocalDateTime.now());

        return logError;
    }

    /**
     * 组装请求参数：queryString 原样保留，body 按形参名映射（跳过响应对象，文件仅记录文件名）。
     */
    private static Map<String, Object> handleParams(HttpServletRequest request, Object[] args, String[] paramNames) {
        Map<String, Object> body = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            String name = paramNames != null && i < paramNames.length ? paramNames[i] : "arg" + i;
            if (arg instanceof MultipartFile file) {
                body.put(name, file.getOriginalFilename());
            } else {
                body.put(name, arg);
            }
        }

        Map<String, Object> params = new HashMap<>();
        params.put("queryString", request == null ? null : request.getQueryString());
        params.put("body", body);
        return params;
    }

    /**
     * 获取当前 Servlet 请求（非请求线程返回 {@code null}）。
     */
    private static HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return attributes instanceof ServletRequestAttributes servletAttributes ? servletAttributes.getRequest() : null;
    }

    /**
     * 用户 ID 安全转换：token 中的 userId 非数字或缺失时返回 0，避免解析异常中断日志链路。
     */
    private static Long parseUserId(String userId) {
        return NumberUtils.toLong(userId, 0L);
    }

}
