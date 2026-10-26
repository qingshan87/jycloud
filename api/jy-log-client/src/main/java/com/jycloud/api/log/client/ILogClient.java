package com.jycloud.api.log.client;

import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.tool.common.constant.ServiceConstant;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * 日志服务（老系统 jy-user）内部接口 —— @HttpExchange 声明式 HTTP 客户端。
 *
 * <p>服务端实现：jy-user 服务 {@code com.jycloud.web.user.client.LogClientController}
 * （{@code /client/log/**}）。
 * 经 {@code LogClientAutoConfiguration} 注册为可直接注入的代理 Bean，按服务名 {@code jy-user}
 * 负载均衡调用；日志写入失败语义由 {@link LogClientFallback} 兜底（仅记录错误，不影响业务）。</p>
 *
 * @author Lqs
 * @date 2022/7/13 9:27
 */
@HttpExchange(url = ILogClient.API_PREFIX, accept = "application/json")
public interface ILogClient {

    String API_PREFIX = ServiceConstant.CLIENT_PATH_PREFIX + "/log";

    /**
     * 写入登录日志
     *
     * @param loginLog 登录日志
     * @return 写入后的登录日志
     */
    @PostExchange("/save/login")
    LoginLog saveLoginLog(@RequestBody LoginLog loginLog);

    /**
     * 写入系统操作日志
     *
     * @param sysLog 系统操作日志
     * @return 写入后的系统操作日志
     */
    @PostExchange("/save/syslog")
    SysLog saveSysLog(@RequestBody SysLog sysLog);

    /**
     * 写入 API 日志
     *
     * @param apiLog API 请求日志
     * @return 写入后的 API 日志
     */
    @PostExchange("/save/apilog")
    SysApiLog saveApiLog(@RequestBody SysApiLog apiLog);

    /**
     * 写入系统错误日志
     *
     * @param logError 系统错误日志
     * @return 写入后的系统错误日志
     */
    @PostExchange("/save/logError")
    LogError saveLogError(@RequestBody LogError logError);

}
