package com.jycloud.web.setting.controller;

import com.jycloud.service.user.service.UserTenantService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户租户表 前端控制器
 * </p>
 *
 * @author jycloud
 * @since 2026-08-27
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user-tenant")
@Tag(name = "用户租户管理", description = "用户租户关联相关接口")
public class UserTenantController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":user-tenant:";

    private final UserTenantService userTenantService;

    @Operation(summary = "将用户移出当前租户")
    @PostMapping("/remove")
    @RequirePermission(PERM_PREFIX + "remove")
    public ServerResponse<Void> remove(@RequestParam Long userId, @RequestParam String tenantId) {
        userTenantService.removeUserFromTenant(userId, tenantId);
        return ServerResponse.successMsg("移除成功");
    }

    @Operation(summary = "邀请用户加入当前租户")
    @PostMapping("/bind")
    @RequirePermission(PERM_PREFIX + "bind")
    public ServerResponse<Void> bind(@RequestParam Long userId,@RequestParam String tenantId) {
        userTenantService.bindUserToTenant(userId, tenantId);
        return ServerResponse.successMsg("邀请成功");
    }
}
