package com.jycloud.api.log.client.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.client.ILogClient;
import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.entity.SysLog;

/**
 * 日志客户端兜底装饰器 —— 等价于原 Feign {@code fallbackFactory} 的容错语义：
 * 日志写入属于旁路行为，失败时仅记录错误日志并返回 {@code null}，绝不影响业务主流程。
 *
 * <p>与 {@link ILogClient} 代理 Bean 分离：业务代码直接注入 {@link ILogClient} 时保持
 * 原始异常可见；事件监听器链路经本装饰器包裹，保证异步/同步执行下业务均不被日志写入失败打断。</p>
 *
 * @author Lqs
 * @date 2022/7/13 9:28
 */
@Slf4j
@RequiredArgsConstructor
public class LogClientFallback implements ILogClient {

    private final ILogClient delegate;

    @Override
    public LoginLog saveLoginLog(LoginLog loginLog) {
        try {
            return delegate.saveLoginLog(loginLog);
        } catch (Exception e) {
            log.error("[LogClientFallback::saveLoginLog] 登录日志写入失败", e);
            return null;
        }
    }

    @Override
    public SysLog saveSysLog(SysLog sysLog) {
        try {
            return delegate.saveSysLog(sysLog);
        } catch (Exception e) {
            log.error("[LogClientFallback::saveSysLog] 系统操作日志写入失败", e);
            return null;
        }
    }

    @Override
    public LogError saveLogError(LogError logError) {
        try {
            return delegate.saveLogError(logError);
        } catch (Exception e) {
            log.error("[LogClientFallback::saveLogError] 系统错误日志写入失败", e);
            return null;
        }
    }

    @Override
    public SysApiLog saveApiLog(SysApiLog apiLog) {
        try {
            return delegate.saveApiLog(apiLog);
        } catch (Exception e) {
            log.error("[LogClientFallback::saveApiLog] API 日志写入失败", e);
            return null;
        }
    }

}
