package com.jycloud.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycloud.api.user.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 租户表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface TenantMapper extends BaseMapper<Tenant> {

    /**
     * 逻辑删除租户
     *
     * @param id        主键
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id,
                   @Param("deleteUid") Long deleteUid);

}
