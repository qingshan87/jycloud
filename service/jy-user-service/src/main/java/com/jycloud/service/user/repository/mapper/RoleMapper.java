package com.jycloud.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycloud.api.user.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-27
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 逻辑删除角色
     *
     * @param roleId    角色 ID
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteById(@Param("roleId") Long roleId,
                   @Param("deleteUid") Long deleteUid);

    /**
     * 批量逻辑删除角色
     *
     * @param roleIds   角色 ID 集合
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteByIds(@Param("roleIds") List<Long> roleIds,
                    @Param("deleteUid") Long deleteUid);
}
