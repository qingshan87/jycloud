package com.jycloud.web.setting.controller;

import com.jycloud.api.user.dto.TenantClientDTO;
import com.jycloud.api.user.vo.TenantClientVO;
import com.jycloud.service.user.service.TenantClientService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 租户授权的应用表 前端控制器
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/tenant-client")
@Tag(name = "租户应用管理", description = "租户绑定应用相关接口")
public class TenantClientController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":tenant-client:";

    private final TenantClientService tenantClientService;

    @Operation(summary = "绑定应用")
    @PostMapping("/bind")
    @RequirePermission(PERM_PREFIX + "set")
    public ServerResponse<Void> bind(@Validated @RequestBody TenantClientDTO dto) {
        tenantClientService.bindClient(dto);
        return ServerResponse.successMsg("绑定成功");
    }

    @Operation(summary = "移除绑定的应用")
    @PostMapping("/unbind/{id}")
    @RequirePermission(PERM_PREFIX + "set")
    public ServerResponse<Void> unbind(@PathVariable Long id) {
        tenantClientService.unbindClient(id);
        return ServerResponse.successMsg("移除成功");
    }

    @Operation(summary = "查询租户绑定的应用列表")
    @GetMapping("/list/{tenantId}")
    public ServerResponse<List<TenantClientVO>> list(@PathVariable String tenantId) {
        return ServerResponse.successData(tenantClientService.listByTenant(tenantId));
    }
}
