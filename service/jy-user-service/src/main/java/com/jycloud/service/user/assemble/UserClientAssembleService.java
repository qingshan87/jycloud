package com.jycloud.service.user.assemble;

import com.jycloud.api.user.constant.UcConstant;
import com.jycloud.api.user.entity.Client;
import com.jycloud.api.user.entity.Menu;
import com.jycloud.api.user.vo.TenantClientVO;
import com.jycloud.api.user.vo.UserClientVO;
import com.jycloud.service.user.service.ClientService;
import com.jycloud.service.user.service.TenantClientService;
import com.jycloud.tool.common.util.DtoEntityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 我的应用聚合服务
 * <p>
 * 组装用户在指定租户下拥有的应用，取并集去重返回，不区分来源：
 * 0) 默认系统：用户中心（1112）每人都有；
 * 1) 权限应用：用户在该租户下关联菜单（含按钮）所属的应用；
 * 2) 三方应用：当前租户已绑定（t_tenant_client 启用）且应用启用的应用，全租户用户可见。
 * </p>
 *
 * @author jycloud
 * @since 2026-08-28
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientAssembleService {

    /** 绑定/应用启用状态 */
    private static final int STATUS_ENABLED = 1;

    private final UserMenuAssembleService userMenuAssembleService;
    private final TenantClientService tenantClientService;
    private final ClientService clientService;

    /**
     * 获取用户在指定租户下拥有的应用列表
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 应用列表
     */
    public List<UserClientVO> listUserClients(Long userId, String tenantId) {
        if (userId == null || StringUtils.isBlank(tenantId)) {
            return Collections.emptyList();
        }

        Set<String> clientIds = new HashSet<>();

        // 0. 默认系统：用户中心每人都有
        clientIds.add(UcConstant.UCENTER_CLIENT_ID);

        // 1. 权限应用：用户菜单（含按钮）所属应用
        List<Menu> menus = userMenuAssembleService.listUserMenus(userId, tenantId);
        if (!CollectionUtils.isEmpty(menus)) {
            menus.stream()
                    .map(Menu::getClientId)
                    .filter(StringUtils::isNotBlank)
                    .forEach(clientIds::add);
        }

        // 2. 三方应用：租户已绑定且启用、应用启用的应用，全租户用户可见
        List<TenantClientVO> tenantClients = tenantClientService.listByTenant(tenantId);
        if (!CollectionUtils.isEmpty(tenantClients)) {
            tenantClients.stream()
                    .filter(vo -> STATUS_ENABLED == vo.getStatus() && STATUS_ENABLED == vo.getClientStatus())
                    .map(TenantClientVO::getClientId)
                    .filter(StringUtils::isNotBlank)
                    .forEach(clientIds::add);
        }

        if (CollectionUtils.isEmpty(clientIds)) {
            return Collections.emptyList();
        }

        // 3. 批量查询启用状态的应用并转换为轻量 VO
        List<Client> clients = clientService.listByClientIds(clientIds);
        if (CollectionUtils.isEmpty(clients)) {
            return Collections.emptyList();
        }
        return clients.stream()
                .sorted(Comparator.comparing(Client::getClientId))
                .map(client -> DtoEntityUtil.trans(client, UserClientVO.class))
                .collect(Collectors.toList());
    }
}
