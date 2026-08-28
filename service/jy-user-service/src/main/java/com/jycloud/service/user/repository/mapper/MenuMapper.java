package com.jycloud.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jycloud.api.user.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    /**
     * 逻辑删除菜单
     *
     * @param id        菜单ID
     * @param deleteUid 删除人ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id,
                   @Param("deleteUid") Long deleteUid);
}
