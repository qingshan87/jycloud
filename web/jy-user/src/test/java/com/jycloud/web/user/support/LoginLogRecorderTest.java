package com.jycloud.web.user.support;

import com.jycloud.api.log.entity.LoginLog;
import com.jycloud.api.user.vo.UserVO;
import com.jycloud.service.auth.dto.AuthDTO;
import com.jycloud.service.auth.vo.AuthInfo;
import com.jycloud.service.user.service.LoginLogService;
import com.jycloud.tool.common.util.AddressUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginLogRecorderTest {

    @Mock
    private LoginLogService loginLogService;

    @Mock
    private AddressUtil addressUtil;

    @InjectMocks
    private LoginLogRecorder loginLogRecorder;

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldRecordCompleteSuccessfulLoginLog() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("1.2.3.4");
        request.addHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(addressUtil.getRegion("1.2.3.4")).thenReturn("中国|浙江省|杭州市|电信|CN");

        UserVO user = new UserVO();
        user.setUserId(12L);
        user.setUsername("tester");
        AuthInfo authInfo = new AuthInfo();
        authInfo.setTenantId("000001");
        authInfo.setUser(user);
        AuthDTO authDTO = buildAuthDTO("000001", "password");

        loginLogRecorder.record(authDTO, authInfo, null);

        LoginLog loginLog = captureSavedLoginLog();
        assertEquals(1, loginLog.getType());
        assertEquals(12L, loginLog.getUserId());
        assertEquals("tester", loginLog.getUsername());
        assertEquals("000001", loginLog.getTenantId());
        assertEquals("登录成功", loginLog.getDescription());
        assertEquals("1.2.3.4", loginLog.getIp());
        assertEquals("中国|浙江省|杭州市|电信|CN", loginLog.getLocation());
        assertEquals("Chrome", loginLog.getBrowser());
        assertEquals("120.0.0.0", loginLog.getBrowserVersion());
        assertNotNull(loginLog.getOperatingSystem());
        assertNotNull(loginLog.getLoginTime());
        assertFalse(loginLog.getUa().isBlank());
    }

    @Test
    void shouldRecordFailureWithDefaultTenantAndZeroUserId() {
        AuthDTO authDTO = buildAuthDTO("", "password");

        loginLogRecorder.record(authDTO, null, new IllegalArgumentException("账号或密码错误"));

        LoginLog loginLog = captureSavedLoginLog();
        assertEquals(0, loginLog.getType());
        assertEquals(0L, loginLog.getUserId());
        assertEquals("tester", loginLog.getUsername());
        assertEquals("000000", loginLog.getTenantId());
        assertEquals("账号或密码错误", loginLog.getDescription());
        assertEquals("password", loginLog.getGrantType());
    }

    private LoginLog captureSavedLoginLog() {
        ArgumentCaptor<LoginLog> captor = ArgumentCaptor.forClass(LoginLog.class);
        verify(loginLogService).save(captor.capture());
        return captor.getValue();
    }

    private static AuthDTO buildAuthDTO(String tenantId, String grantType) {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setTenantId(tenantId);
        authDTO.setUsername("tester");
        authDTO.setGrantType(grantType);
        return authDTO;
    }
}
