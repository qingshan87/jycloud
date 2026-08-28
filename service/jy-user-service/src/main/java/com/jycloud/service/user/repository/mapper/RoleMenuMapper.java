package com.jycloud.service.user.repository.mapper;

import com.jycloud.api.user.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * <p>
 * 角色菜单表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    /**
     * 查询绑定了指定菜单的角色所属的租户 ID 集合
     *
     * @param menuId 菜单 ID
     * @return 租户 ID 集合
     */
    Set<String> listTenantIdsByMenuId(@Param("menuId") Long menuId);
}
