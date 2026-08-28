package com.jycloud.tool.common.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 客户端 IP 获取工具
 *
 * <p>代理头优先级：
 * X-Forwarded-For → X-Real-IP → Proxy-Client-IP → WL-Proxy-Client-IP → RemoteAddr</p>
 *
 * @author jycloud
 */
@Slf4j
public final class IpUtil {

    private IpUtil() {}

    /**
     * 从当前请求上下文获取客户端 IP
     */
    public static String getClientIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return null;
            }
            return getClientIp(attrs.getRequest());
        } catch (Exception e) {
            log.warn("获取客户端 IP 失败", e);
            return null;
        }
    }

    /**
     * 从 HttpServletRequest 中获取客户端 IP
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        // log.info("从 HttpServletRequest 中获取客户端 IP", request);

        String ip = request.getHeader("X-Forwarded-For");
        if (isInvalid(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isInvalid(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isInvalid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isInvalid(ip)) {
            ip = request.getRemoteAddr();
        }

        // X-Forwarded-For 可能包含多级代理 IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    private static boolean isInvalid(String ip) {
        return StringUtils.isBlank(ip);
    }

}
