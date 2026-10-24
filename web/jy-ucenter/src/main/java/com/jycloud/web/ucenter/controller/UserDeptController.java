package com.jycloud.web.ucenter.controller;

import com.jycloud.api.user.dto.UserDeptDTO;
import com.jycloud.api.user.entity.UserDept;
import com.jycloud.service.user.service.UserDeptService;
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

/**
 * 用户部门 Controller
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user-dept")
@Tag(name = "用户部门管理", description = "用户部门关联相关接口")
public class UserDeptController {

    private static final String PERM_PREFIX = ServiceConstant.UCENTER_NAME + ":user-dept:";

    private final UserDeptService userDeptService;

    @Operation(summary = "设置用户部门（传空 deptId 表示解绑）")
    @PostMapping("/set")
    @RequirePermission(PERM_PREFIX + "set")
    public ServerResponse<Void> set(@Validated @RequestBody UserDeptDTO dto) {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        userDeptService.setUserDept(dto.getUserId(), dto.getDeptId());
        return ServerResponse.successMsg("设置成功");
    }

    @Operation(summary = "查询用户部门")
    @GetMapping("/get")
    public ServerResponse<UserDept> get(@RequestParam Long userId) {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        return ServerResponse.successData(userDeptService.getUserDept(userId, tenantId));
    }
}
