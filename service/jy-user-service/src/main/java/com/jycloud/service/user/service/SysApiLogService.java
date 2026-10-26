package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.log.dto.SysApiLogQueryDTO;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.vo.SysApiLogVO;

/**
 * <p>
 * API 调用日志 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
public interface SysApiLogService extends IService<SysApiLog> {

    /**
     * 条件分页查询 API 调用日志
     *
     * @param queryDTO 查询条件
     * @return API 调用日志分页结果
     */
    IPage<SysApiLogVO> pageByCondition(SysApiLogQueryDTO queryDTO);
}
