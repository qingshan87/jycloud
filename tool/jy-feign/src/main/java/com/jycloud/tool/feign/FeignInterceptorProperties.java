package com.jycloud.tool.feign;

import com.jycloud.tool.common.constant.HeaderConstant;
import com.jycloud.tool.common.constant.TokenConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "jy.feign")
public class FeignInterceptorProperties {
    /** 允许透传的 Header 白名单（默认仅业务必要字段） */
    private List<String> allowedHeaders = List.of(
            HeaderConstant.TENANT_ID,
            HeaderConstant.CLIENT_ID,
            HeaderConstant.PROJECT_ID,
            HeaderConstant.TRACE_ID,
            HeaderConstant.REQUEST_ID
    );

    /** Token 头名称 */
    private String tokenHeader = TokenConstant.TOKEN;
}