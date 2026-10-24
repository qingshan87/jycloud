package com.jycloud.service.user.repository.mapper;

import com.jycloud.api.user.entity.Dept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 部门表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept> {

    /**
     * 逻辑删除部门
     *
     * @param deptId    部门 ID
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteById(@Param("deptId") Long deptId,
                   @Param("deleteUid") Long deleteUid);
}
