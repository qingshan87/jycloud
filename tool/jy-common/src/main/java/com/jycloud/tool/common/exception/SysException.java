package com.jycloud.tool.common.exception;

import com.jycloud.tool.common.api.ResponseCode;
import lombok.Getter;


/**
 * Bling 接口运行时异常
 * Created by evanosc on 2019/6/19.
 */
@Getter
public class SysException extends RuntimeException{



    private static final long serialVersionUID = -994962710559017255L;


    private String message;

    private Integer code;

    public SysException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SysException(String message) {
        super(message);
        this.message = message;
    }

    public SysException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public SysException(SysException sysException) {
        super(sysException);
        this.code = sysException.getCode();
        this.message = sysException.getMessage();
    }


}
