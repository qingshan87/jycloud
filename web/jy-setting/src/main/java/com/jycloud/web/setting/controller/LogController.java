package com.jycloud.web.setting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.log.dto.LogErrorQueryDTO;
import com.jycloud.api.log.dto.LoginLogQueryDTO;
import com.jycloud.api.log.dto.SysApiLogQueryDTO;
import com.jycloud.api.log.dto.SysLogQueryDTO;
import com.jycloud.api.log.vo.LogErrorVO;
import com.jycloud.api.log.vo.LoginLogVO;
import com.jycloud.api.log.vo.SysApiLogVO;
import com.jycloud.api.log.vo.SysLogVO;
import com.jycloud.service.user.service.LogErrorService;
import com.jycloud.service.user.service.LoginLogService;
import com.jycloud.service.user.service.SysApiLogService;
import com.jycloud.service.user.service.SysLogService;
import com.jycloud.tool.common.api.ServerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 日志管理 前端控制器
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/log")
@Tag(name = "日志管理", description = "登录、操作、API和错误日志查询接口")
public class LogController {

    private final LoginLogService loginLogService;
    private final SysLogService sysLogService;
    private final SysApiLogService sysApiLogService;
    private final LogErrorService logErrorService;

    @Operation(summary = "条件分页查询登录日志")
    @GetMapping("/login/page")
    public ServerResponse<IPage<LoginLogVO>> pageLogin(
            @ParameterObject @Validated LoginLogQueryDTO queryDTO) {
        return ServerResponse.successData(loginLogService.pageByCondition(queryDTO));
    }

    @Operation(summary = "条件分页查询系统操作日志")
    @GetMapping("/operation/page")
    public ServerResponse<IPage<SysLogVO>> pageOperation(
            @ParameterObject @Validated SysLogQueryDTO queryDTO) {
        return ServerResponse.successData(sysLogService.pageByCondition(queryDTO));
    }

    @Operation(summary = "条件分页查询API调用日志")
    @GetMapping("/api/page")
    public ServerResponse<IPage<SysApiLogVO>> pageApi(
            @ParameterObject @Validated SysApiLogQueryDTO queryDTO) {
        return ServerResponse.successData(sysApiLogService.pageByCondition(queryDTO));
    }

    @Operation(summary = "条件分页查询系统错误日志")
    @GetMapping("/error/page")
    public ServerResponse<IPage<LogErrorVO>> pageError(
            @ParameterObject @Validated LogErrorQueryDTO queryDTO) {
        return ServerResponse.successData(logErrorService.pageByCondition(queryDTO));
    }
}
