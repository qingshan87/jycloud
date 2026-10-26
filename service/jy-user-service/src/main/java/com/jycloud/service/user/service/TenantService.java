package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.user.dto.TenantQueryDTO;
import com.jycloud.api.user.entity.Tenant;
import com.jycloud.api.user.vo.TenantVO;

/**
 * <p>
 * 租户表 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
public interface TenantService extends IService<Tenant> {

    /**
     * 新增租户（自动生成 tenantId）
     *
     * @param tenant 租户实体
     * @return 保存后的租户
     */
    Tenant create(Tenant tenant);

    /**
     * 修改租户（tenantId 不允许修改）
     *
     * @param tenant 租户实体
     * @return 更新后的租户
     */
    Tenant update(Tenant tenant);

    /**
     * 分页查询租户列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<TenantVO> pageByCondition(TenantQueryDTO queryDTO);

    /**
     * 根据主键查询租户
     *
     * @param id 主键
     * @return 租户
     */
    Tenant getTenantById(Long id);

    /**
     * 根据租户ID查询租户
     *
     * @param tenantId 租户ID
     * @return 租户
     */
    Tenant getTenantByTenantId(String tenantId);

    /**
     * 删除租户（默认租户不允许删除）
     *
     * @param id 主键
     */
    void deleteTenant(Long id);

}
