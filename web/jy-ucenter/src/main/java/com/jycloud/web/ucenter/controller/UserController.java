package com.jycloud.web.ucenter.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.jycloud.tool.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller
 *
 * @author jycloud
 * @since 2026-07-23
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {

    private static final String PERM_PREFIX = ServiceConstant.UCENTER_NAME + ":user:";

    private final UserService userService;

    @Operation(summary = "条件分页查询用户列表")
    @GetMapping("/page")
    public ServerResponse<IPage<UserVO>> page(@ParameterObject @Validated UserQueryDTO dto) {
        String tenantId = UserContext.getTenantId();
        IPage<UserVO> page = userService.pageByTenantId(dto, tenantId);
        return ServerResponse.successData(page);
    }

    @Operation(summary = "新增用户")
    @PostMapping("/create")
    @RequirePermission(PERM_PREFIX + "create")
    public ServerResponse<UserVO> create(@Validated(ValidationGroups.Insert.class) @RequestBody UserDTO dto) {
        User user = DtoEntityUtil.trans(dto, User.class);
        User result = userService.create(user);
        UserVO vo = DtoEntityUtil.trans(result, UserVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "修改用户")
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

    @Operation(summary = "重置密码")
    @Parameter(name = "userIds", description = "用户ID集合，多个用英文逗号分隔", required = true)
    @PostMapping("/password/reset")
    @RequirePermission(PERM_PREFIX + "resetPassword")
    public ServerResponse resetPassword(@RequestParam("userIds") List<Long> userIds) {
        userService.resetPassword(userIds);
        return ServerResponse.successMsg("重置密码成功");
    }


}
