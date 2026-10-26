package com.jycloud.api.log.utils;

import com.jycloud.api.log.annotation.Log;
import com.jycloud.api.log.entity.SysLog;
import com.jycloud.api.log.properties.SysLogProperties;
import com.jycloud.tool.common.context.LoginVal;
import com.jycloud.tool.common.context.UserContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SysLogUtilTest {

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void shouldBuildLogFromUserContext() throws Exception {
        LoginVal loginVal = new LoginVal();
        loginVal.setUserId("12");
        loginVal.setUserName("tester");
        loginVal.setTenantId("000000");
        UserContext.set(loginVal);

        SysLogProperties properties = new SysLogProperties();
        properties.setApplicationName("jy-test");
        SysLogUtil util = new SysLogUtil(properties);

        Method method = DemoService.class.getDeclaredMethod("save", String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);
        when(signature.getName()).thenReturn("save");

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getTarget()).thenReturn(new DemoService());
        when(joinPoint.getArgs()).thenReturn(new Object[]{"order-1"});

        SysLog result = util.createLog(joinPoint);

        assertEquals("000000", result.getTenantId());
        assertEquals("tester", result.getUsername());
        assertEquals(12L, result.getCreateUid());
        assertEquals("jy-test", result.getServiceId());
        assertEquals("新增订单", result.getDescription());
    }

    static class DemoService {

        @Log("新增订单")
        public void save(String orderNo) {
            // test fixture
        }
    }
}
