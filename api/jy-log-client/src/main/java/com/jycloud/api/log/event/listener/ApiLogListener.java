package com.jycloud.api.log.event.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.entity.SysApiLog;
import com.jycloud.api.log.event.ApiLogEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.function.Consumer;


/**
 * 异步监听日志事件
 *
 * @author zuihou
 * @date 2019-07-01 15:13
 */
@Slf4j
@RequiredArgsConstructor
public class ApiLogListener {

    final Consumer<SysApiLog> consumer;

    @Async
    @EventListener(ApiLogEvent.class)
    public void saveApiLog(ApiLogEvent event) {
        SysApiLog log = (SysApiLog) event.getSource();

        consumer.accept(log);
    }


}
