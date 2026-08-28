package com.jycloud.service.auth.service;

import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.vo.AuthInfo;

public interface ITokenGranter {

    /**
     * 授权
     *
     * @param authDTO 授权参数
     * @return 授权信息
     * @throws Exception
     */
    AuthInfo grant(AuthDTO authDTO) throws Exception;

    /**
     * 授权类型标识
     *
     * @return 授权类型, 对应 GrantTypeEnum 的 code
     */
    String grantType();
}
