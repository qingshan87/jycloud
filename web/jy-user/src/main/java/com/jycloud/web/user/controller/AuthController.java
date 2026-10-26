package com.jycloud.web.user.controller;

import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.service.ITokenGranter;
import com.jycloud.service.auth.service.TokenGranterBuilder;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.cache.TokenCache;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.web.user.support.LoginLogRecorder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证 Controller
 *
 * @author jycloud
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/auth")
@Tag(name = "认证管理", description = "登录/退出相关接口")
public class AuthController {

    private final TokenGranterBuilder tokenGranterBuilder;
    private final TokenCache tokenCache;
    private final LoginLogRecorder loginLogRecorder;

    /**
     * 登录：根据授权类型分发到对应的 TokenGranter
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public ServerResponse<AuthInfo> login(@Valid @RequestBody AuthDTO authDTO) {
        ITokenGranter granter = tokenGranterBuilder.getGranter(authDTO.getGrantType());
        AuthInfo authInfo;
        try {
            authInfo = granter.grant(authDTO);
            loginLogRecorder.record(authDTO, authInfo, null);
        } catch (SysException e) {
            loginLogRecorder.record(authDTO, null, e);
            throw e;
        } catch (Exception e) {
            log.error("登录失败, grantType={}", authDTO.getGrantType(), e);
            loginLogRecorder.record(authDTO, null, e);
            throw new SysException(ResponseCode.SYSTEM_INTERNAL_ERROR);
        }
        return ServerResponse.successData(authInfo);
    }

    /**
     * 退出登录：删除服务端 token 并清理当前上下文
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ServerResponse<Void> logout() {
        String userId = UserContext.getUserId();
        if (StringUtils.isNotBlank(userId)) {
            tokenCache.deleteToken(userId);
        }
        UserContext.clear();
        return ServerResponse.successMsg("退出成功");
    }

}
