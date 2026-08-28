package com.jycloud.service.user.repository.mapper;

import com.jycloud.api.user.entity.ClientVersions;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 应用版本管理记录 Mapper 接口
 * </p>
 *
 * @author jycloud
 * @since 2026-07-24
 */
@Mapper
public interface ClientVersionsMapper extends BaseMapper<ClientVersions> {

}
