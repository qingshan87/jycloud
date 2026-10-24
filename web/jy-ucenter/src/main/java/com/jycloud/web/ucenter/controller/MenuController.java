package com.jycloud.web.ucenter.controller;

import com.jycloud.api.user.vo.TenantClientVO;
import com.jycloud.service.user.assemble.TenantMenuAssembleService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.context.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单查询 Controller（只读，菜单维护在设置平台）
 * <p>
 * 提供当前租户按应用分类的菜单树，供角色菜单分配、用户资源分配使用。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/menu")
@Tag(name = "菜单查询", description = "租户菜单树查询（只读）")
public class MenuController {

    private final TenantMenuAssembleService tenantMenuAssembleService;

    @Operation(summary = "查询当前租户按应用分类的菜单树")
    @GetMapping("/tree")
    public ServerResponse<List<TenantClientVO>> tree() {
        String tenantId = UserContext.getTenantId();
        if (StringUtils.isBlank(tenantId)) {
            return ServerResponse.errorMsg("租户ID不能为空");
        }
        return ServerResponse.successData(tenantMenuAssembleService.listTenantClientMenuTree(tenantId));
    }
}
