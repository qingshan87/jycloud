package com.jycloud.web.user.feign;

import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.feign.UserAuthorityFeign;
import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.service.user.assemble.UserMenuAssembleService;
import com.jycloud.service.user.assemble.UserPermissionAssembleService;
import com.jycloud.service.user.assemble.UserRoleAssembleService;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.util.DtoEntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户权限 Feign 接口实现
 * <p>
 * 向业务系统提供用户角色 / 权限标识 / 菜单数据，
 * 全部走用户中心本地缓存优先的聚合服务（{@link UserRoleAssembleService} 等），
 * 与鉴权切面（UserRoleProvider / UserPermissionProvider）数据口径一致。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserAuthorityFeignController implements UserAuthorityFeign {

    private final UserRoleAssembleService userRoleAssembleService;
    private final UserPermissionAssembleService userPermissionAssembleService;
    private final UserMenuAssembleService userMenuAssembleService;

    @Override
    public ServerResponse<Set<String>> getRoles(String userId, String tenantId) {
        Long uid = parseUserId(userId);
        if (uid == null) {
            return ServerResponse.errorMsg("非法 userId");
        }
        Set<String> roles = userRoleAssembleService.listUserRoles(uid, tenantId).stream()
                .map(Role::getRoleName)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        return ServerResponse.successData(roles);
    }

    @Override
    public ServerResponse<Set<String>> getPermissions(String userId, String tenantId) {
        Long uid = parseUserId(userId);
        if (uid == null) {
            return ServerResponse.errorMsg("非法 userId");
        }
        return ServerResponse.successData(userPermissionAssembleService.listPermissions(uid, tenantId));
    }

    @Override
    public ServerResponse<List<MenuVO>> getUserMenus(String userId, String tenantId) {
        Long uid = parseUserId(userId);
        if (uid == null) {
            return ServerResponse.errorMsg("非法 userId");
        }
        List<Menu> menus = userMenuAssembleService.listUserMenus(uid, tenantId);
        List<MenuVO> vos = menus.stream()
                .map(menu -> DtoEntityUtil.trans(menu, MenuVO.class))
                .collect(Collectors.toList());
        return ServerResponse.successData(vos);
    }

    @Override
    public ServerResponse<List<MenuVO>> getUserMenuTree(String userId, String tenantId, String clientId) {
        Long uid = parseUserId(userId);
        if (uid == null) {
            return ServerResponse.errorMsg("非法 userId");
        }
        return ServerResponse.successData(userMenuAssembleService.listUserMenuTree(uid, tenantId, clientId));
    }

    @Override
    public ServerResponse<List<Long>> getUserMenuIds(String userId, String tenantId) {
        Long uid = parseUserId(userId);
        if (uid == null) {
            return ServerResponse.errorMsg("非法 userId");
        }
        return ServerResponse.successData(userMenuAssembleService.listUserMenuIds(uid, tenantId));
    }

    /**
     * 解析用户 ID，非法返回 null
     */
    private Long parseUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        try {
            return Long.valueOf(userId);
        } catch (NumberFormatException e) {
            log.warn("【Feign 用户权限查询】非法 userId: {}", userId);
            return null;
        }
    }
}
