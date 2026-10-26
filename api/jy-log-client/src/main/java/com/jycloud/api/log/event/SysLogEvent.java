package com.jycloud.api.log.event;

import lombok.Getter;
import lombok.Setter;
import com.jycloud.api.log.entity.SysLog;
import org.springframework.context.ApplicationEvent;

/**
 * 日志事件
 */
@Getter
@Setter
public class SysLogEvent extends ApplicationEvent {

    public SysLogEvent(SysLog source) {
        super(source);
    }
}
