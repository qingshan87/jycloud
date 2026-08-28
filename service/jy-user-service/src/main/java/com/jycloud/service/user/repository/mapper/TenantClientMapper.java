package com.jycloud.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycloud.api.user.entity.TenantClient;
import com.jycloud.api.user.vo.TenantClientVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 租户授权的应用表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface TenantClientMapper extends BaseMapper<TenantClient> {

    /**
     * 查询租户绑定的应用列表（联表应用信息）
     *
     * @param tenantId 租户ID
     * @return 应用列表
     */
    List<TenantClientVO> selectTenantClientVOList(@Param("tenantId") String tenantId);

    /**
     * 查询指定租户和应用的绑定记录（含已逻辑删除）
     *
     * @param tenantId 租户ID
     * @param clientId 应用ID
     * @return 绑定记录
     */
    TenantClient selectIncludeDeleted(@Param("tenantId") String tenantId,
                                      @Param("clientId") String clientId);

    /**
     * 重新启用已逻辑删除的绑定记录
     *
     * @param id     主键
     * @param status 绑定状态
     * @return 影响行数
     */
    int revive(@Param("id") Long id,
               @Param("status") Integer status);

    /**
     * 逻辑删除租户应用绑定
     *
     * @param id        主键
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id,
                   @Param("deleteUid") Long deleteUid);

}
