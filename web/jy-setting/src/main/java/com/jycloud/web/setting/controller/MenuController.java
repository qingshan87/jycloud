package com.jycloud.web.setting.controller;

import com.jycloud.api.user.dto.MenuDTO;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.service.user.service.MenuService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理 Controller
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/menu")
@Tag(name = "菜单管理", description = "应用菜单与按钮相关接口")
public class MenuController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":menu:";

    private final MenuService menuService;

    @Operation(summary = "新增菜单/按钮")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<MenuVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody MenuDTO dto) {
        Menu menu = DtoEntityUtil.trans(dto, Menu.class);
        Menu result = menuService.create(menu);
        return ServerResponse.successData(DtoEntityUtil.trans(result, MenuVO.class));
    }

    @Operation(summary = "修改菜单/按钮")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<MenuVO> update(@Validated(ValidationGroups.Update.class) @RequestBody MenuDTO dto) {
        Menu menu = DtoEntityUtil.trans(dto, Menu.class);
        Menu result = menuService.update(menu);
        return ServerResponse.successData(DtoEntityUtil.trans(result, MenuVO.class));
    }

    @Operation(summary = "根据ID查询菜单/按钮")
    @GetMapping("/{menuId}")
    public ServerResponse<MenuVO> getById(@PathVariable Long menuId) {
        Menu menu = menuService.getMenuById(menuId);
        if (menu == null) {
            return ServerResponse.errorMsg("菜单不存在");
        }
        return ServerResponse.successData(DtoEntityUtil.trans(menu, MenuVO.class));
    }

    @Operation(summary = "查询应用菜单树")
    @GetMapping("/tree/{clientId}")
    public ServerResponse<List<MenuVO>> tree(@PathVariable String clientId) {
        return ServerResponse.successData(menuService.listMenuTree(clientId));
    }

    @Operation(summary = "删除菜单/按钮")
    @PostMapping("/delete/{menuId}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long menuId) {
        menuService.deleteMenu(menuId);
        return ServerResponse.successMsg("删除成功");
    }
}
