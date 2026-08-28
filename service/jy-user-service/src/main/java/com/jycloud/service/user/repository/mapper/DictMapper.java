package com.jycloud.service.user.repository.mapper;

import com.jycloud.api.user.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 字典表 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface DictMapper extends BaseMapper<Dict> {

    /**
     * 逻辑删除字典（含级联删除时逐个调用）
     *
     * @param dictId    主键
     * @param deleteUid 删除人 ID
     * @return 影响行数
     */
    int deleteById(@Param("dictId") Long dictId,
                   @Param("deleteUid") Long deleteUid);

}
