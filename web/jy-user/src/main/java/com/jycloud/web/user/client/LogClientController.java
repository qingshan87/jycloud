package com.jycloud.web.user.client;

import com.jycloud.api.log.client.ILogClient;
import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.service.user.service.LogErrorService;
import com.jycloud.service.user.service.LoginLogService;
import com.jycloud.service.user.service.SysApiLogService;
import com.jycloud.service.user.service.SysLogService;
import com.jycloud.tool.common.api.ResponseCode;
import com.jycloud.tool.common.exception.SysException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志客户端服务端实现。
 */
@RestController
@RequiredArgsConstructor
public class LogClientController implements ILogClient {

    private final LoginLogService loginLogService;
    private final SysLogService sysLogService;
    private final SysApiLogService sysApiLogService;
    private final LogErrorService logErrorService;

    @Override
    public LoginLog saveLoginLog(LoginLog loginLog) {
        loginLogService.save(requireLog(loginLog));
        return loginLog;
    }

    @Override
    public SysLog saveSysLog(SysLog sysLog) {
        sysLogService.save(requireLog(sysLog));
        return sysLog;
    }

    @Override
    public SysApiLog saveApiLog(SysApiLog apiLog) {
        sysApiLogService.save(requireLog(apiLog));
        return apiLog;
    }

    @Override
    public LogError saveLogError(LogError logError) {
        logErrorService.save(requireLog(logError));
        return logError;
    }

    private static <T> T requireLog(T log) {
        if (log == null) {
            throw new SysException(ResponseCode.PARAMETER_IS_INCORRECT);
        }
        return log;
    }
}
