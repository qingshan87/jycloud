package com.jycloud.api.log.event.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jycloud.api.log.entity.LogError;
import com.jycloud.api.log.event.LogErrorEvent;
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
public class LogErrorListener {

    final Consumer<LogError> consumer;

    @Async
    @EventListener(LogErrorEvent.class)
    public void saveLogError(LogErrorEvent event) {
        LogError logError = (LogError) event.getSource();

        consumer.accept(logError);
    }


}
