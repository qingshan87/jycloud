package com.jycloud.web.ucenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.user.dto.RoleDTO;
import com.jycloud.api.user.dto.RoleQueryDTO;
import com.jycloud.api.user.vo.RoleVO;
import com.jycloud.service.user.service.RoleService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理 Controller
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/role")
@Tag(name = "角色管理", description = "角色相关接口")
public class RoleController {

    private static final String PERM_PREFIX = ServiceConstant.UCENTER_NAME + ":role:";

    private final RoleService roleService;

    @Operation(summary = "新增角色")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<Void> create(@Validated(ValidationGroups.Insert.class) @RequestBody RoleDTO dto) {
        roleService.createRole(dto);
        return ServerResponse.successMsg("创建成功");
    }

    @Operation(summary = "修改角色")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<Void> update(@Validated(ValidationGroups.Update.class) @RequestBody RoleDTO dto) {
        roleService.updateRole(dto);
        return ServerResponse.successMsg("修改成功");
    }

    @Operation(summary = "条件分页查询当前租户角色列表")
    @GetMapping("/page")
    public ServerResponse<IPage<RoleVO>> page(@ParameterObject @Validated RoleQueryDTO dto) {
        dto.setTenantId(UserContext.getTenantId());
        dto.setReadonly(0);
        IPage<RoleVO> page = roleService.pageByCondition(dto);
        return ServerResponse.successData(page);
    }

    @Operation(summary = "查询当前租户的角色列表,用于角色设置的下拉框")
    @GetMapping("/list")
    public ServerResponse<List<RoleVO>> list() {
        List<RoleVO> list = roleService.listByTenantId(UserContext.getTenantId(), null);
        return ServerResponse.successData(list);
    }

    @Operation(summary = "角色详情")
    @GetMapping("/detail/{roleId}")
    public ServerResponse<RoleVO> detail(@PathVariable Long roleId) {
        return ServerResponse.successData(roleService.detail(roleId));
    }

    @Operation(summary = "删除角色")
    @PostMapping("/delete/{roleId}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ServerResponse.successMsg("删除成功");
    }
}
