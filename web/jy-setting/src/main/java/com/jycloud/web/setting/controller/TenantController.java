package com.jycloud.web.setting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.log.annotation.Log;
import com.jycloud.api.user.dto.TenantDTO;
import com.jycloud.api.user.dto.TenantQueryDTO;
import com.jycloud.api.user.entity.Tenant;
import com.jycloud.api.user.vo.TenantVO;
import com.jycloud.service.user.service.TenantService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 租户表 前端控制器
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/tenant")
@Tag(name = "租户管理", description = "租户相关接口")
public class TenantController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":tenant:";

    private final TenantService tenantService;

    @Operation(summary = "新增租户")
    @Log("新增租户")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<TenantVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody TenantDTO dto) {
        Tenant tenant = DtoEntityUtil.trans(dto, Tenant.class);
        Tenant result = tenantService.create(tenant);
        TenantVO vo = DtoEntityUtil.trans(result, TenantVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "修改租户")
    @Log("修改租户")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<TenantVO> update(@Validated(ValidationGroups.Update.class) @RequestBody TenantDTO dto) {
        Tenant tenant = DtoEntityUtil.trans(dto, Tenant.class);
        Tenant result = tenantService.update(tenant);
        TenantVO vo = DtoEntityUtil.trans(result, TenantVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "根据ID查询租户")
    @GetMapping("/{id}")
    public ServerResponse<TenantVO> getById(@PathVariable Long id) {
        Tenant tenant = tenantService.getTenantById(id);
        if (tenant == null) {
            return ServerResponse.errorMsg("租户不存在");
        }
        TenantVO vo = DtoEntityUtil.trans(tenant, TenantVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "条件分页查询租户列表")
    @GetMapping("/page")
    public ServerResponse<IPage<TenantVO>> page(@ParameterObject @Validated TenantQueryDTO dto) {
        IPage<TenantVO> page = tenantService.pageByCondition(dto);
        return ServerResponse.successData(page);
    }

    @Operation(summary = "删除租户")
    @Log("删除租户")
    @PostMapping("/delete/{id}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long id) {
        tenantService.deleteTenant(id);
        return ServerResponse.successMsg("删除成功");
    }
}
