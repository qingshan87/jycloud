package com.jycloud.service.user.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycloud.api.user.dto.ClientQueryDTO;
import com.jycloud.api.user.entity.Client;
import com.jycloud.api.user.vo.ClientVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 业务系统表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface ClientMapper extends BaseMapper<Client> {

    /**
     * 条件分页查询业务系统列表（映射为 VO，不返回敏感字段）
     */
    IPage<ClientVO> selectClientPage(Page<ClientVO> page,
                                     @Param("queryDTO") ClientQueryDTO queryDTO);

    /**
     * 逻辑删除业务系统
     *
     * @param id        主键
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id,
                   @Param("deleteUid") Long deleteUid);

}
