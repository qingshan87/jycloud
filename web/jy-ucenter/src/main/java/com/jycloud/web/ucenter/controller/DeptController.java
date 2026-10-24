package com.jycloud.web.ucenter.controller;

import com.jycloud.api.user.dto.DeptDTO;
import com.jycloud.api.user.entity.Dept;
import com.jycloud.api.user.vo.DeptVO;
import com.jycloud.service.user.service.DeptService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.util.DtoEntityUtil;
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
 * 部门管理 Controller
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/dept")
@Tag(name = "部门管理", description = "部门相关接口")
public class DeptController {

    private static final String PERM_PREFIX = ServiceConstant.UCENTER_NAME + ":dept:";

    private final DeptService deptService;

    @Operation(summary = "查询当前租户部门树")
    @GetMapping("/tree")
    public ServerResponse<List<DeptVO>> tree() {
        String tenantId = UserContext.getTenantId();
        return ServerResponse.successData(deptService.listDeptTree(tenantId));
    }

    @Operation(summary = "根据ID查询部门")
    @GetMapping("/{deptId}")
    public ServerResponse<DeptVO> getById(@PathVariable Long deptId) {
        Dept dept = deptService.getDeptById(deptId);
        if (dept == null) {
            return ServerResponse.errorMsg("部门不存在");
        }
        return ServerResponse.successData(DtoEntityUtil.trans(dept, DeptVO.class));
    }

    @Operation(summary = "新增部门")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<DeptVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody DeptDTO dto) {
        Dept dept = DtoEntityUtil.trans(dto, Dept.class);
        Dept result = deptService.createDept(dept);
        return ServerResponse.successData(DtoEntityUtil.trans(result, DeptVO.class));
    }

    @Operation(summary = "修改部门")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<DeptVO> update(@Validated(ValidationGroups.Update.class) @RequestBody DeptDTO dto) {
        Dept dept = DtoEntityUtil.trans(dto, Dept.class);
        Dept result = deptService.updateDept(dept);
        return ServerResponse.successData(DtoEntityUtil.trans(result, DeptVO.class));
    }

    @Operation(summary = "删除部门")
    @PostMapping("/delete/{deptId}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long deptId) {
        deptService.deleteDept(deptId);
        return ServerResponse.successMsg("删除成功");
    }
}
