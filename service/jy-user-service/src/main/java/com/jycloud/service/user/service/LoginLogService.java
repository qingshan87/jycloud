package com.jycloud.service.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jycloud.api.log.dto.LoginLogQueryDTO;
import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.api.log.vo.LoginLogVO;

/**
 * <p>
 * 登录日志 服务类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
public interface LoginLogService extends IService<LoginLog> {

    /**
     * 条件分页查询登录日志
     *
     * @param queryDTO 查询条件
     * @return 登录日志分页结果
     */
    IPage<LoginLogVO> pageByCondition(LoginLogQueryDTO queryDTO);
}
