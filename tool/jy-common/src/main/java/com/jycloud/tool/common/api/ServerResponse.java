package com.jycloud.tool.common.api;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Optional;

/**
 * 统一服务响应类,方法返回值
 */
@Getter
@Setter
@NoArgsConstructor
public class ServerResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    private int code;
    private String msg;
    private T data;


    private ServerResponse(int code) {
        this(code, null, null);
    }

    private ServerResponse(int code, String msg) {
        this(code, msg, null);
    }

    /**
     * 为了避免T 为string的时候调用的是 上面的构造方法，需要对构造方法进行包装。构造方法设置为私有，不允许外部调用
     *
     * @param code 　响应代码
     * @param data 　相应数据
     */
    private ServerResponse(int code, T data) {
        this(code, null, data);
    }


    private ServerResponse(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 判断返回是否为成功
     *
     * @param result Result
     * @return 是否成功
     */
    public static boolean isSuccess(@Nullable ServerResponse<?> result) {
        return Optional.ofNullable(result)
                .map(x -> ObjectUtils.nullSafeEquals(ResponseCode.SUCCESS.getCode(), x.code))
                .orElse(Boolean.FALSE);
    }


    //请求成功
    public static <T> ServerResponse<T> successData(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> ServerResponse<T> successMsg(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> successMsgData(String msg, T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    //请求失败
    public static <T> ServerResponse<T> errorMsg(String msg) {
        return new ServerResponse<T>(ResponseCode.FAILURE.getCode(), msg);
    }

    public static <T> ServerResponse<T> errorCodeMsg(int code, String msg) {
        return new ServerResponse<T>(code, msg);
    }

    public static <T> ServerResponse<T> errorCodeMsgData(int code, String msg, T data) {
        return new ServerResponse<>(code, msg, data);
    }

    public static <T> ServerResponse<T> errorResponseCode(ResponseCode responseCode) {
        return new ServerResponse<>(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> ServerResponse<T> errorResponseCode(ResponseCode responseCode, T data) {
        return new ServerResponse<>(responseCode.getCode(), responseCode.getMessage(),data);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("code", getCode())
                .append("msg", getMsg())
                .append("data", getData())
                .toString();
    }


}
