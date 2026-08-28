package com.jycloud.service.user.repository.mapper;

import com.jycloud.api.user.entity.UserTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycloud.api.user.vo.UserTenantVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户租户表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface UserTenantMapper extends BaseMapper<UserTenant> {

    /**
     * 查询用户关联的租户列表
     */
    List<UserTenantVO> selectUserTenantVOList(@Param("userId") Long userId);
}
