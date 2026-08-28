package com.jycloud.service.auth.service;

import com.jycloud.tool.common.exception.SysException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenGranterBuilder {

    private final List<ITokenGranter> tokenGranters;
    private Map<String, ITokenGranter> granterMap;

    @PostConstruct
    public void init() {
        granterMap = tokenGranters.stream()
                .collect(Collectors.toMap(ITokenGranter::grantType, Function.identity()));
    }

    /**
     * 根据授权类型获取对应的 TokenGranter
     *
     * @param grantType 授权类型
     * @return ITokenGranter 实现
     * @throws SysException 不支持的授权类型
     */
    public ITokenGranter getGranter(String grantType) {
        ITokenGranter granter = granterMap.get(grantType);
        if (granter == null) {
            throw new SysException("不支持的授权类型: " + grantType);
        }
        return granter;
    }
}
