package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.log.dto.LogErrorQueryDTO;
import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.vo.LogErrorVO;

/**
 * <p>
 * 系统错误日志 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
public interface LogErrorService extends IService<LogError> {

    /**
     * 条件分页查询系统错误日志
     *
     * @param queryDTO 查询条件
     * @return 系统错误日志分页结果
     */
    IPage<LogErrorVO> pageByCondition(LogErrorQueryDTO queryDTO);
}
