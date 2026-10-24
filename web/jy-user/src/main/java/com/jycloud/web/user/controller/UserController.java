package com.jycloud.web.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jycloud.api.user.dto.PasswordDTO;
import com.jycloud.api.user.dto.UserDTO;
import com.jycloud.api.user.dto.UserProfileDTO;
import com.jycloud.api.user.dto.UserQueryDTO;
import com.jycloud.api.user.entity.User;
import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.api.user.vo.UserClientVO;
import com.jycloud.api.user.vo.UserVO;
import com.jycloud.service.user.assemble.UserClientAssembleService;
import com.jycloud.service.user.assemble.UserDetailAssembleService;
import com.jycloud.service.user.assemble.UserMenuAssembleService;
import com.jycloud.service.user.service.UserService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.base.ValidationGroups;
import com.jycloud.tool.common.context.UserContext;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.common.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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

    private final UserService userService;
    private final UserMenuAssembleService userMenuAssembleService;
    private final UserDetailAssembleService userDetailAssembleService;
    private final UserClientAssembleService userClientAssembleService;

    @Operation(summary = "修改密码（当前登录用户）")
    @PostMapping("/password/update")
    public ServerResponse<Void> updatePassword(@Validated @RequestBody PasswordDTO dto) {
        userService.updatePassword(dto.getOldPassword(), dto.getNewPassword());
        return ServerResponse.successMsg("修改成功");
    }

    @Operation(summary = "编辑个人信息（当前登录用户）")
    @PostMapping("/profile/update")
    public ServerResponse<UserVO> updateProfile(@Validated @RequestBody UserProfileDTO dto) {
        User user = DtoEntityUtil.trans(dto, User.class);
        User result = userService.updateProfile(user);
        UserVO vo = DtoEntityUtil.trans(result, UserVO.class);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "获取我的应用列表")
    @GetMapping("/apps")
    public ServerResponse<List<UserClientVO>> getMyApps() {
        String tenantId = UserContext.getTenantId();
        String userId = UserContext.getUserId();
        List<UserClientVO> list = userClientAssembleService.listUserClients(Long.valueOf(userId), tenantId);
        return ServerResponse.successData(list);
    }

    @Operation(summary = "获取用户路由菜单")
    @GetMapping("/router/menu")
    public ServerResponse<List<MenuVO>> getUserRouterMenu() {
        String tenantId = UserContext.getTenantId();
        String clientId = UserContext.getClientId();
        String userId = UserContext.getUserId();
        List<MenuVO> menuList = userMenuAssembleService.listUserMenuTree(Long.valueOf(userId), tenantId, clientId);
        return ServerResponse.successData(menuList);
    }

    @Operation(summary = "根据ID查询用户，不传ID时查询当前登录用户")
    @GetMapping(value = {"", "/{userId}"})
    public ServerResponse<UserVO> getById(@PathVariable(required = false) Long userId) {
        if (userId == null) {
            String currentUserId = UserContext.getUserId();
            if (currentUserId == null) {
                return ServerResponse.errorMsg("用户ID不能为空");
            }
            userId = Long.valueOf(currentUserId);
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            return ServerResponse.errorMsg("用户不存在");
        }
        UserVO vo = DtoEntityUtil.trans(user, UserVO.class);
        String tenantId = UserContext.getTenantId();
        userDetailAssembleService.fillAssociations(List.of(vo), tenantId);
        return ServerResponse.successData(vo);
    }

    @Operation(summary = "根据关键字查询用户信息")
    @GetMapping("/keyword")
    public ServerResponse<List<UserVO>> getByKeyword(@RequestParam String keyword) {
        String tenantId = UserContext.getTenantId();
        List<UserVO> list = userService.listByTenantIdAndKeyword(keyword, tenantId);
        return ServerResponse.successData(list);
    }

}
