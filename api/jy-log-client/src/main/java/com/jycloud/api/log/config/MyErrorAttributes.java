package com.jycloud.api.log.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.utils.SysLogUtil;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.util.DtoEntityUtil;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * 全局错误响应定制 —— 替换 Boot 默认 ErrorAttributes：统一 ServerResponse 结构，
 * 并在存在异常时发布服务异常事件（写错误日志）。
 *
 * <p>经 {@code LogClientAutoConfiguration} 注册为 Bean（Servlet Web 环境生效），
 * 不再是组件扫描配置类；Boot 4 起 {@link DefaultErrorAttributes} 位于
 * {@code org.springframework.boot.webmvc.error} 包，错误属性名使用 {@code jakarta.servlet.error.*} 前缀。</p>
 *
 * @author Lqs
 * @date 2022/7/25 16:04
 */
@Slf4j
@RequiredArgsConstructor
public class MyErrorAttributes extends DefaultErrorAttributes {

    private final SysLogUtil sysLogUtil;

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        String requestUri = this.getAttr(webRequest, "jakarta.servlet.error.request_uri");
        Integer status = this.getAttr(webRequest, "jakarta.servlet.error.status_code");
        int statusCode = status == null ? ResponseCode.SYSTEM_INTERNAL_ERROR.getCode() : status;
        Throwable error = getError(webRequest);
        ServerResponse result;
        if (error == null) {
            log.error("URL:{} error status:{}", requestUri, statusCode);
            result = ServerResponse.errorCodeMsg(ResponseCode.SYSTEM_INTERNAL_ERROR.getCode(),
                    "系统未知异常[HttpStatus]:" + statusCode);
        } else {
            log.error(String.format("URL:%s error status:%d", requestUri, statusCode), error);
            result = ServerResponse.errorCodeMsg(statusCode, error.getMessage());
        }

        //发送服务异常事件
        if (error != null) {
            sysLogUtil.publishLogErrorEvent(error);
        }
        return DtoEntityUtil.trans(result, Map.class);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private <T> T getAttr(WebRequest webRequest, String name) {
        return (T) webRequest.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

}
