package com.jycloud.web.setting.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.log.annotation.Log;
import com.jycloud.api.user.dto.InviteUserQueryDTO;
import com.jycloud.api.user.dto.UserDTO;
import com.jycloud.api.user.dto.UserQueryDTO;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.vo.UserVO;
import com.jycloud.service.user.service.UserService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.constant.ServiceConstant;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.common.util.IpUtil;
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private static final String PERM_PREFIX = ServiceConstant.SETTING_NAME + ":user:";

    private final UserService userService;

    @Operation(summary = "新增用户")
    @Log("新增用户")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<UserVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody UserDTO dto) {
        User user = DtoEntityUtil.trans(dto, User.class);
        User result = StringUtils.isNotBlank(dto.getTenantId())
                ? userService.createForTenant(user, dto.getTenantId())
                : userService.create(user);
        UserVO vo = DtoEntityUtil.trans(result, UserVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "修改用户")
    @Log("修改用户")
    @PostMapping("/update")
    @RequirePermission(PERM_PREFIX + "update")
    public ServerResponse<UserVO> update(@Validated(ValidationGroups.Update.class) @RequestBody UserDTO dto) {
        User user = DtoEntityUtil.trans(dto, User.class);
        User result = userService.update(user);
        UserVO vo = DtoEntityUtil.trans(result, UserVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{userId}")
    public ServerResponse<UserVO> getById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ServerResponse.errorMsg("用户不存在");
        }
        UserVO vo = DtoEntityUtil.trans(user, UserVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "条件分页查询租户用户列表")
    @GetMapping("/page/tenant")
    public ServerResponse<IPage<UserVO>> pageByTenantId(@ParameterObject @Validated UserQueryDTO dto, @RequestParam String tenantId) {
        IPage<UserVO> page = userService.pageByTenantId(dto, tenantId);
        return ServerResponse.successData(page);
    }

    @Operation(summary = "条件分页查询系统用户列表")
    @GetMapping("/page/system")
    public ServerResponse<IPage<UserVO>> page(@ParameterObject @Validated UserQueryDTO dto) {
        return ServerResponse.successData(userService.pageByCondition(dto));
    }

    @Operation(summary = "查询可邀请加入租户的系统用户（需携带查询条件，无条件返回空）")
    @GetMapping("/invite")
    public ServerResponse<List<UserVO>> pageInviteUsers(@ParameterObject @Validated InviteUserQueryDTO dto, @RequestParam String tenantId) {
        // 不按租户过滤，直接查询系统用户；未携带查询条件时返回空列表
        return ServerResponse.successData(userService.listInviteUsers(dto));
    }

    @Operation(summary = "删除用户")
    @Log("删除用户")
    @PostMapping("/delete/{userId}")
    @RequirePermission(PERM_PREFIX + "delete")
    public ServerResponse<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ServerResponse.successMsg("删除成功");
    }

    @Operation(summary = "重置密码")
    @Log("重置用户密码")
    @PostMapping("/password/reset")
    @RequirePermission(PERM_PREFIX + "resetPassword")
    public ServerResponse<Void> resetPassword(@RequestParam("userIds") List<Long> userIds) {
        userService.resetPassword(userIds);
        return ServerResponse.successMsg("重置密码成功");
    }



}
