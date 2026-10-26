package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.user.dto.TenantClientDTO;
import com.jycloud.api.user.entity.TenantClient;
import com.jycloud.api.user.vo.TenantClientVO;

import java.util.List;

/**
 * <p>
 * 租户授权的应用表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface TenantClientService extends IService<TenantClient> {

    /**
     * 绑定应用（授权应用给租户）
     *
     * @param dto 绑定请求
     */
    void bindClient(TenantClientDTO dto);

    /**
     * 移除租户绑定的应用
     *
     * @param id 绑定主键
     */
    void unbindClient(Long id);

    /**
     * 查询租户绑定的应用列表（含应用信息）
     *
     * @param tenantId 租户ID
     * @return 应用列表
     */
    List<TenantClientVO> listByTenant(String tenantId);
}
