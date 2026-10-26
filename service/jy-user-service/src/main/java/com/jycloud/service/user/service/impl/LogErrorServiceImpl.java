package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.log.dto.LogErrorQueryDTO;
import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.vo.LogErrorVO;
import com.jycloud.service.user.repository.mapper.LogErrorMapper;
import com.jycloud.service.user.service.LogErrorService;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 系统错误日志 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LogErrorServiceImpl extends ServiceImpl<LogErrorMapper, LogError> implements LogErrorService {

    @Override
    public IPage<LogErrorVO> pageByCondition(LogErrorQueryDTO queryDTO) {
        Page<LogError> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "createTime", SortUtil.ORDER_DESC, true);

        LogError entity = DtoEntityUtil.trans(queryDTO, LogError.class);
        LambdaQueryWrapper<LogError> wrapper = new LambdaQueryWrapper<>(entity)
                .ge(queryDTO.getBeginTime() != null, LogError::getCreateTime, queryDTO.getBeginTime())
                .le(queryDTO.getEndTime() != null, LogError::getCreateTime, queryDTO.getEndTime());

        return this.page(page, wrapper)
                .convert(logError -> DtoEntityUtil.trans(logError, LogErrorVO.class));
    }
}
