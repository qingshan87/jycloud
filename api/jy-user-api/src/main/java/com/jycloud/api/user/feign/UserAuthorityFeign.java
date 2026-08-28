package com.jycloud.api.user.feign;

import com.jycloud.api.user.vo.MenuVO;
import com.jycloud.tool.common.api.ServerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * 用户权限 Feign 接口
 * <p>
 * 供业务系统远程获取用户中心（jy-user）的用户角色、权限标识与菜单数据。
 * 业务系统禁止直接依赖 jy-user-service，统一通过本接口调用；
 * 实现见 jy-user 服务 {@code com.jycloud.web.user.feign.UserAuthorityFeignController}。
 * 参数由调用方显式传入（userId / tenantId），不依赖登录上下文隐式获取。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Tag(name = "[Feign]用户权限", description = "供业务系统远程获取用户角色/权限/菜单")
@FeignClient(name = "jy-user", path = "/feign/user-authority")
public interface UserAuthorityFeign {

    /**
     * 获取用户在指定租户下的角色标识集合（role_name）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 角色名集合
     */
    @Operation(summary = "获取用户角色标识集合")
    @GetMapping("/roles")
    ServerResponse<Set<String>> getRoles(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取用户在指定租户下的权限标识集合（perms）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 权限标识集合
     */
    @Operation(summary = "获取用户权限标识集合")
    @GetMapping("/permissions")
    ServerResponse<Set<String>> getPermissions(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);

    /**
     * 获取用户在指定租户下的全部菜单（含按钮，已按租户菜单白名单过滤）
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 菜单列表
     */
    @Operation(summary = "获取用户所用菜单列表")
    @GetMapping("/menus")
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
    @GetMapping("/menu-tree")
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
    @GetMapping("/menu-ids")
    ServerResponse<List<Long>> getUserMenuIds(@RequestParam("userId") String userId, @RequestParam("tenantId") String tenantId);
}
