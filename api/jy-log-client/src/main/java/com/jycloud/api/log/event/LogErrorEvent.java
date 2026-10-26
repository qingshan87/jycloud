package com.jycloud.api.log.event;

import lombok.Getter;
import lombok.Setter;
import com.jycloud.api.log.entity.LogError;
import org.springframework.context.ApplicationEvent;

/**
 * 日志事件
 */
@Getter
@Setter
public class LogErrorEvent extends ApplicationEvent {

    public LogErrorEvent(LogError source) {
        super(source);
    }
}
