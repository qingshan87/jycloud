package com.jycloud.tool.feign;

import com.jycloud.tool.common.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class FeignHeaderInterceptor implements RequestInterceptor {

    private final List<String> allowedHeaders;
    private final String tokenHeader;

    @Override
    public void apply(RequestTemplate template) {
        try {
            // 1. 优先从 UserContext 获取 Token（兼容 @Async / 内部调用 / 定时任务）
            String token = UserContext.getToken();

            // 2. 降级：从当前请求头获取（仅同步 HTTP 请求有效）
            if (StringUtils.isBlank(token)) {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    token = attrs.getRequest().getHeader(tokenHeader);
                }
            }

            // 3. 注入 Token（避免重复添加）
            if (StringUtils.isNotBlank(token)) {
                template.header(tokenHeader, token);
            }

            // 4. 白名单透传业务 Header
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null && allowedHeaders != null) {
                HttpServletRequest request = attrs.getRequest();
                for (String headerName : allowedHeaders) {
                    String value = request.getHeader(headerName);
                    if (StringUtils.isNotBlank(value)) {
                        template.header(headerName, value);
                    }
                }
            }
        } catch (IllegalStateException e) {
            // WebFlux / 非 Servlet 环境安全降级（如 Gateway 中调用）
            log.debug("非 Servlet 环境，跳过 Feign Header 透传");
        } catch (Exception e) {
            log.error("Feign Header 透传异常", e);
        }
    }
}