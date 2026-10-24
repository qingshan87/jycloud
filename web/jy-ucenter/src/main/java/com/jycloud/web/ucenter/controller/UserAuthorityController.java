package com.jycloud.web.ucenter.controller;

import com.jycloud.api.user.constant.AuthorityCategoryEnum;
import com.jycloud.api.user.dto.UserAuthorityDTO;
import com.jycloud.service.user.service.UserAuthorityService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 用户角色/资源绑定 Controller
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user-authority")
@Tag(name = "用户角色/资源管理", description = "用户角色与资源绑定相关接口")
public class UserAuthorityController {

    private static final String PERM_PREFIX = ServiceConstant.UCENTER_NAME + ":user-authority:";

    private final UserAuthorityService userAuthorityService;

    @Operation(summary = "设置用户角色")
    @PostMapping("/set-role")
    @RequirePermission(PERM_PREFIX + "set")
    public ServerResponse<Void> setRole(@Validated @RequestBody UserAuthorityDTO dto) {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        userAuthorityService.setUserAuthorities(dto.getUserId(), tenantId,
                AuthorityCategoryEnum.ROLE.getCode(), dto.getDataIds());
        return ServerResponse.successMsg("设置成功");
    }

    @Operation(summary = "查询用户绑定的角色ID集合")
    @GetMapping("/list-role")
    public ServerResponse<Set<Long>> listRole(@RequestParam Long userId) {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        Set<Long> roleIds = userAuthorityService.listDataIds(userId, tenantId,
                AuthorityCategoryEnum.ROLE.getCode());
        return ServerResponse.successData(roleIds);
    }

    @Operation(summary = "设置用户资源（菜单）")
    @PostMapping("/set-menu")
    @RequirePermission(PERM_PREFIX + "set")
    public ServerResponse<Void> setMenu(@Validated @RequestBody UserAuthorityDTO dto) {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        userAuthorityService.setUserAuthorities(dto.getUserId(), tenantId,
                AuthorityCategoryEnum.MENU.getCode(), dto.getDataIds());
        return ServerResponse.successMsg("设置成功");
    }

    @Operation(summary = "查询用户绑定的资源（菜单）ID集合")
    @GetMapping("/list-menu")
    public ServerResponse<Set<Long>> listMenu(@RequestParam Long userId) {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        Set<Long> menuIds = userAuthorityService.listDataIds(userId, tenantId,
                AuthorityCategoryEnum.MENU.getCode());
        return ServerResponse.successData(menuIds);
    }
}
