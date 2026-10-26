package com.jycloud.tool.http;

import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.constant.TokenConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * RestClient 请求头透传拦截器。
 *
 * <p>Servlet 请求线程透传入站请求头；异步线程从 {@link UserContext} 兜底透传 Token。</p>
 */
@Slf4j
public class UserContextRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Set<String> IGNORED_HEADERS = Set.of(
            "content-length",
            "host", "connection", "expect", "upgrade",
            "transfer-encoding", "keep-alive", "te", "trailer",
            "proxy-authenticate", "proxy-authorization");

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                relayToken(request);
            } else {
                relayHeaders(request, attributes.getRequest());
            }
        } catch (Exception e) {
            log.error("RestClient 请求头透传失败, uri={}", request.getURI(), e);
        }
        return execution.execute(request, body);
    }

    private static void relayToken(HttpRequest request) {
        String token = UserContext.getToken();
        if (StringUtils.isNotBlank(token)) {
            request.getHeaders().set(TokenConstant.TOKEN, token);
        } else {
            log.debug("非请求线程且无用户上下文，跳过请求头透传, uri={}", request.getURI());
        }
    }

    private static void relayHeaders(HttpRequest request, HttpServletRequest source) {
        Enumeration<String> headerNames = source.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (IGNORED_HEADERS.contains(name.toLowerCase(Locale.ROOT))) {
                continue;
            }
            List<String> values = Collections.list(source.getHeaders(name));
            if (!values.isEmpty()) {
                request.getHeaders().put(name, values);
            }
        }
    }
}
