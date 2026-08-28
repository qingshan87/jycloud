package com.jycloud.web.setting.controller;

import com.jycloud.api.user.constant.AuthorityCategoryEnum;
import com.jycloud.api.user.dto.UserRoleDTO;
import com.jycloud.service.user.service.UserAuthorityService;
import com.jycloud.tool.common.api.ServerResponse;
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
 * 用户角色绑定 Controller
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user-role")
@Tag(name = "用户角色管理", description = "用户角色绑定相关接口")
public class UserRoleController {

    private final UserAuthorityService userAuthorityService;

    @Operation(summary = "设置用户角色")
    @PostMapping("/set")
    public ServerResponse<Void> set(@Validated @RequestBody UserRoleDTO dto) {
        if (StringUtils.isBlank(dto.getTenantId())) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        userAuthorityService.setUserAuthorities(
                dto.getUserId(),
                dto.getTenantId(),
                AuthorityCategoryEnum.ROLE.getCode(),
                dto.getRoleIds());
        return ServerResponse.successMsg("设置成功");
    }

    @Operation(summary = "查询用户绑定的角色ID集合")
    @GetMapping("/list")
    public ServerResponse<Set<Long>> list(@RequestParam Long userId, @RequestParam String tenantId) {
        Set<Long> roleIds = userAuthorityService.listDataIds(userId, tenantId, AuthorityCategoryEnum.ROLE.getCode());
        return ServerResponse.successData(roleIds);
    }
}
