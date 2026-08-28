package com.jycloud.service.user.assemble;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jycloud.api.user.entity.Role;
import com.jycloud.api.user.entity.UserDataPermission;
import com.jycloud.api.user.entity.UserDept;
import com.jycloud.api.user.vo.UserVO;
import com.jycloud.service.user.service.UserDataPermissionService;
import com.jycloud.service.user.service.UserDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户详情聚合服务
 * <p>
 * 负责给 {@link UserVO} 批量填充角色、部门、数据权限等关联信息。
 * 角色走缓存优先的 {@link UserRoleAssembleService}（缓存+版本号惰性失效）；
 * 部门与数据权限批量 IN 查询一次取回后按 userId 映射，避免 N+1。
 * </p>
 *
 * @author jycloud
 * @since 2026-08
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailAssembleService {

    private final UserRoleAssembleService userRoleAssembleService;
    private final UserDeptService userDeptService;
    private final UserDataPermissionService userDataPermissionService;

    /**
     * 批量填充用户列表的角色、部门、数据权限关联信息
     *
     * @param vos      用户 VO 列表（分页 records）
     * @param tenantId 租户 ID
     * @return 填充完成后的用户 VO 列表
     */
    public List<UserVO> fillAssociations(List<UserVO> vos, String tenantId) {
        if (CollectionUtils.isEmpty(vos) || StringUtils.isBlank(tenantId)) {
            return vos;
        }
        List<Long> userIds = vos.stream()
                .map(UserVO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return vos;
        }

        Map<Long, UserDept> deptMap = this.listUserDeptMap(userIds, tenantId);
        Map<Long, UserDataPermission> permissionMap = this.listDataPermissionMap(userIds, tenantId);

        for (UserVO vo : vos) {
            vo.setUserDept(deptMap.get(vo.getUserId()));
            vo.setDataPermission(permissionMap.get(vo.getUserId()));
            vo.setRoles(userRoleAssembleService.listUserRoles(vo.getUserId(), tenantId));
        }
        return vos;
    }

    /**
     * 批量查询用户部门并按 userId 映射（同一用户仅保留一条）
     */
    private Map<Long, UserDept> listUserDeptMap(List<Long> userIds, String tenantId) {
        List<UserDept> list = userDeptService.list(new LambdaQueryWrapper<UserDept>()
                .in(UserDept::getUserId, userIds)
                .eq(UserDept::getTenantId, tenantId));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(UserDept::getUserId, Function.identity(), (a, b) -> a));
    }

    /**
     * 批量查询用户数据权限并按 userId 映射（同一用户仅保留一条）
     */
    private Map<Long, UserDataPermission> listDataPermissionMap(List<Long> userIds, String tenantId) {
        List<UserDataPermission> list = userDataPermissionService.list(new LambdaQueryWrapper<UserDataPermission>()
                .in(UserDataPermission::getUserId, userIds)
                .eq(UserDataPermission::getTenantId, tenantId));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(UserDataPermission::getUserId, Function.identity(), (a, b) -> a));
    }
}
