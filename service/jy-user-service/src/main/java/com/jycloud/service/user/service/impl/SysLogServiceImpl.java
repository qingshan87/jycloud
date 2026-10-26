package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.log.dto.SysLogQueryDTO;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.api.log.vo.SysLogVO;
import com.jycloud.service.user.repository.mapper.SysLogMapper;
import com.jycloud.service.user.service.SysLogService;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 系统操作日志 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Override
    public IPage<SysLogVO> pageByCondition(SysLogQueryDTO queryDTO) {
        Page<SysLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "createTime", SortUtil.ORDER_DESC, true);

        SysLog entity = DtoEntityUtil.trans(queryDTO, SysLog.class);
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>(entity)
                .ge(queryDTO.getBeginTime() != null, SysLog::getCreateTime, queryDTO.getBeginTime())
                .le(queryDTO.getEndTime() != null, SysLog::getCreateTime, queryDTO.getEndTime());

        return this.page(page, wrapper)
                .convert(sysLog -> DtoEntityUtil.trans(sysLog, SysLogVO.class));
    }
}
