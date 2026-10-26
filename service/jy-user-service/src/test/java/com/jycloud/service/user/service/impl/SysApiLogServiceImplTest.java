package com.jycloud.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jycloud.api.log.dto.SysApiLogQueryDTO;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.vo.SysApiLogVO;
import com.jycloud.api.user.entity.Client;
import com.jycloud.service.user.repository.mapper.SysApiLogMapper;
import com.jycloud.service.user.service.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SysApiLogServiceImplTest {

    @Mock
    private SysApiLogMapper sysApiLogMapper;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private SysApiLogServiceImpl sysApiLogService;

    @Test
    void shouldFillClientNameWhenPagingApiLogs() {
        ReflectionTestUtils.setField(sysApiLogService, "baseMapper", sysApiLogMapper);

        SysApiLog apiLog = new SysApiLog();
        apiLog.setId(1L);
        apiLog.setClientId("1111");
        apiLog.setServiceName("jy-user");

        Page<SysApiLog> page = new Page<>(1, 10);
        page.setRecords(List.of(apiLog));
        page.setTotal(1);
        when(sysApiLogMapper.selectPage(any(Page.class), any(Wrapper.class))).thenAnswer(invocation -> {
            Page<SysApiLog> requestedPage = invocation.getArgument(0);
            requestedPage.setRecords(page.getRecords());
            requestedPage.setTotal(page.getTotal());
            return requestedPage;
        });

        Client client = new Client();
        client.setClientId("1111");
        client.setAppName("设置平台");
        when(clientService.listByClientIds(List.of("1111"))).thenReturn(List.of(client));

        SysApiLogVO record = sysApiLogService.pageByCondition(new SysApiLogQueryDTO()).getRecords().getFirst();

        assertEquals("1111", record.getClientId());
        assertEquals("设置平台", record.getClientName());
    }
}
