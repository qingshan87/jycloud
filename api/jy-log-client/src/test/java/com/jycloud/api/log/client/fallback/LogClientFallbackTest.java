package com.jycloud.api.log.client.fallback;

import com.jycloud.api.log.client.ILogClient;
import com.jycloud.api.log.entity.SysLog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogClientFallbackTest {

    @Test
    void shouldNotBreakBusinessFlowWhenLogWriteFails() {
        ILogClient delegate = mock(ILogClient.class);
        when(delegate.saveSysLog(any())).thenThrow(new IllegalStateException("unavailable"));

        SysLog result = new LogClientFallback(delegate).saveSysLog(new SysLog());

        assertNull(result);
    }
}
