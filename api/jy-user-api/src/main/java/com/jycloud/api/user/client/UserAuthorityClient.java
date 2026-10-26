package com.jycloud.api.user.client;

import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.tool.common.api.ServerResponse;
import com.jycloud.tool.common.constant.ServiceConstant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.Set;

/**
 * 用户权限 HTTP 服务客户端契约
 * <p>
 * 供业务系统远程获取用户中心（jy-user）的用户角色、权限标识与菜单数据。
 * 客户端使用 Spring {@link HttpExchange} 声明式 HTTP 接口，
 * 由 {@code UserAuthorityClientAutoConfiguration} 创建基于 RestClient 的动态代理。
 * 服务端实现见 {@code com.jycloud.web.user.client.UserAuthorityClientController}。
 * 参数由调用方显式传入（userId / tenantId），不依赖登录上下文隐式获取。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Tag(name = "用户权限服务客户端", description = "供业务系统远程获取用户角色/权限/菜单")
@HttpExchange(url = ServiceConstant.CLIENT_PATH_PREFIX + "/user-authority", accept = "application/json")
public interface UserAuthorityClient {

    /**
     * 获取用户在指定租户下的角色标识集合（role_name）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 角色名集合
     */
    @Operation(summary = "获取用户角色标识集合")
    @GetExchange("/roles")
    ServerResponse<Set<String>> getRoles(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取用户在指定租户下的权限标识集合（perms）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 权限标识集合
     */
    @Operation(summary = "获取用户权限标识集合")
    @GetExchange("/permissions")
    ServerResponse<Set<String>> getPermissions(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取用户在指定租户下的全部菜单（含按钮，已按租户菜单白名单过滤）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 菜单列表
     */
    @Operation(summary = "获取用户所用菜单列表")
    @GetExchange("/menus")
    ServerResponse<List<MenuVO>> getUserMenus(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取用户在指定租户下指定应用（clientId）的菜单树（仅菜单，父子集结构）
     * <p>
     * 按 clientId 过滤出当前应用的菜单数据，并仅保留菜单类型（category=0，剔除按钮），
     * 用于前端生成应用 router。
     * </p>
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @param clientId 应用 ID
     * @return 菜单树（父子集结构，仅含菜单类型）
     */
    @Operation(summary = "获取用户应用下菜单树（父子集结构，仅含菜单类型）")
    @GetExchange("/menu-tree")
    ServerResponse<List<MenuVO>> getUserMenuTree(@RequestParam("userId") String userId,
                                                 @RequestParam("tenantId") String tenantId,
                                                 @RequestParam("clientId") String clientId);

    /**
     * 获取用户在指定租户下关联的 menuId 集合（含按钮）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 菜单 ID 集合
     */
    @Operation(summary = "获取用户菜单ID集合")
    @GetExchange("/menu-ids")
    ServerResponse<List<Long>> getUserMenuIds(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);
}
