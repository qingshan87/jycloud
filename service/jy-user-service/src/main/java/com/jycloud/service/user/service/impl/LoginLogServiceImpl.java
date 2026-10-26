package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.log.dto.LoginLogQueryDTO;
import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.api.log.vo.LoginLogVO;
import com.jycloud.service.user.repository.mapper.LoginLogMapper;
import com.jycloud.service.user.service.LoginLogService;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 登录日志 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public IPage<LoginLogVO> pageByCondition(LoginLogQueryDTO queryDTO) {
        Page<LoginLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "loginTime", SortUtil.ORDER_DESC, true);

        LoginLog entity = DtoEntityUtil.trans(queryDTO, LoginLog.class);
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>(entity)
                .ge(queryDTO.getBeginTime() != null, LoginLog::getLoginTime, queryDTO.getBeginTime())
                .le(queryDTO.getEndTime() != null, LoginLog::getLoginTime, queryDTO.getEndTime());

        return this.page(page, wrapper)
                .convert(e -> DtoEntityUtil.trans(e, LoginLogVO.class));
    }
}
