package com.jycloud.web.user.handler;

import com.jycloud.tool.common.constant.HeaderConstant;
import com.jycloud.tool.common.constant.TokenConstant;
import com.jycloud.tool.common.context.LoginVal;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.exception.SysException;
import com.jycloud.tool.common.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;


/**
 * @author Lqs
 * @date 2025/1/5 16:42
 */
@Component
public class TokenHandler implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String headerToken = request.getHeader(TokenConstant.TOKEN);
        if (StringUtils.isNotBlank(headerToken)) {
            String token = JWTUtil.decryptToken(request.getHeader(TokenConstant.TOKEN));
            String tenantId = JWTUtil.getTenantId(token);
            String userId = JWTUtil.getUserId(token);
            String userName = JWTUtil.getUsername(token);
            String clientId = JWTUtil.getHeader(HeaderConstant.CLIENT_ID);
            String projectId = JWTUtil.getHeader(HeaderConstant.PROJECT_ID);
            String traceId = JWTUtil.getHeader(HeaderConstant.TRACE_ID);
            if (StringUtils.isBlank(userId)) {
                throw new SysException("无法解析出有效的用户信息");
            } else {
                LoginVal user = new LoginVal();
                user.setToken(headerToken);//原Token
                user.setTenantId(tenantId);
                user.setUserId(userId);
                user.setUserName(userName);
                user.setClientId(clientId);
                user.setProjectId(projectId);
                user.setTraceId(traceId);

                UserContext.set(user);
            }
        }
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.clear();
    }
}
