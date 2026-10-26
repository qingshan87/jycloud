package com.jycloud.api.log.client;

import com.jycloud.api.log.entity.SysLog;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ILogClientTest {

    @Test
    void shouldWriteSystemLogThroughHttpExchange() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://jy-user");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        ILogClient client = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(builder.build()))
                .build()
                .createClient(ILogClient.class);

        server.expect(requestTo("http://jy-user/client/log/save/syslog"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(
                        "{\"id\":1,\"serviceId\":\"jy-test\",\"type\":1}",
                        MediaType.APPLICATION_JSON));

        SysLog result = client.saveSysLog(new SysLog()
                .setServiceId("jy-test")
                .setType(1));

        assertEquals(1L, result.getId());
        assertEquals("jy-test", result.getServiceId());
        server.verify();
    }
}
