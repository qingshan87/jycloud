package com.jycloud.service.user.repository.mapper;

import com.jycloud.api.user.entity.UserAuthority;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * 用户权限关联表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface UserAuthorityMapper extends BaseMapper<UserAuthority> {

    /**
     * 查询用户在该租户下拥有的角色标识（role_name）集合
     *
     * @param userId   用户 ID
     * @param tenantId 租户 ID
     * @return 角色名集合
     */
    Set<String> selectRoleNames(@Param("userId") Long userId, @Param("tenantId") String tenantId);

}
