package com.jycloud.api.log.event;

import lombok.Getter;
import lombok.Setter;
import com.jycloud.api.log.entity.SysApiLog;
import org.springframework.context.ApplicationEvent;

/**
 * 日志事件
 */
@Getter
@Setter
public class ApiLogEvent extends ApplicationEvent {

    public ApiLogEvent(SysApiLog source) {
        super(source);
    }
}
