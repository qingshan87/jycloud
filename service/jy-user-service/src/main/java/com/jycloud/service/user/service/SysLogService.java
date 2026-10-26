package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.log.dto.SysLogQueryDTO;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.api.log.vo.SysLogVO;

/**
 * <p>
 * 系统操作日志 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 条件分页查询系统操作日志
     *
     * @param queryDTO 查询条件
     * @return 系统操作日志分页结果
     */
    IPage<SysLogVO> pageByCondition(SysLogQueryDTO queryDTO);
}
