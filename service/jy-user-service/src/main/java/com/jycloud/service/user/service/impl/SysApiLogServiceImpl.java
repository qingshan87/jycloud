package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jycloud.api.log.dto.SysApiLogQueryDTO;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.vo.SysApiLogVO;
import com.jycloud.api.user.entity.Client;
import com.jycloud.service.user.repository.mapper.SysApiLogMapper;
import com.jycloud.service.user.service.ClientService;
import com.jycloud.service.user.service.SysApiLogService;
import com.jycloud.tool.common.util.DtoEntityUtil;
import com.jycloud.tool.mp.utils.SortUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * API 调用日志 服务实现类
 * </p>
 *
 * @author jycloud
 * @since 2026-10-24
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class SysApiLogServiceImpl extends ServiceImpl<SysApiLogMapper, SysApiLog> implements SysApiLogService {

    private final ClientService clientService;

    @Override
    public IPage<SysApiLogVO> pageByCondition(SysApiLogQueryDTO queryDTO) {
        Page<SysApiLog> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        SortUtil.handlePageSortMulti(queryDTO, page, "createTime", SortUtil.ORDER_DESC, true);

        SysApiLog entity = DtoEntityUtil.trans(queryDTO, SysApiLog.class);
        LambdaQueryWrapper<SysApiLog> wrapper = new LambdaQueryWrapper<>(entity)
                .ge(queryDTO.getBeginTime() != null, SysApiLog::getCreateTime, queryDTO.getBeginTime())
                .le(queryDTO.getEndTime() != null, SysApiLog::getCreateTime, queryDTO.getEndTime());

        IPage<SysApiLogVO> result = this.page(page, wrapper)
                .convert(apiLog -> DtoEntityUtil.trans(apiLog, SysApiLogVO.class));
        fillClientNames(result.getRecords());
        return result;
    }

    /**
     * 批量补全发起 API 请求的应用名称，避免逐行查询。
     */
    private void fillClientNames(List<SysApiLogVO> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        List<String> clientIds = records.stream()
                .map(SysApiLogVO::getClientId)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .toList();
        if (clientIds.isEmpty()) {
            return;
        }

        Map<String, String> clientNameMap = new HashMap<>();
        for (Client client : clientService.listByClientIds(clientIds)) {
            if (client != null && StringUtils.isNotBlank(client.getClientId())) {
                clientNameMap.put(client.getClientId(), client.getAppName());
            }
        }
        records.forEach(record -> record.setClientName(clientNameMap.get(record.getClientId())));
    }
}
