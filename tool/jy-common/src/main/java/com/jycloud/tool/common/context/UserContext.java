package com.jycloud.tool.common.context;

import com.alibaba.ttl.TransmittableThreadLocal;
public class UserContext {

    private static final TransmittableThreadLocal<LoginVal> USER_INFO_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static void set(LoginVal user) {
        USER_INFO_THREAD_LOCAL.set(user);
    }

    public static LoginVal get() {
        return USER_INFO_THREAD_LOCAL.get();
    }

    public static String getToken() {
        LoginVal user = USER_INFO_THREAD_LOCAL.get();
        return user == null ? null : user.getToken();
    }

    public static String getUserId() {
        LoginVal user = USER_INFO_THREAD_LOCAL.get();
        return user == null ? null : user.getUserId();
    }

    public static String getUserName() {
        LoginVal user = USER_INFO_THREAD_LOCAL.get();
        return user == null ? null : user.getUserName();
    }

    public static String getTenantId() {
        LoginVal user = USER_INFO_THREAD_LOCAL.get();
        return user == null ? null : user.getTenantId();
    }

    public static String getClientId() {
        LoginVal user = USER_INFO_THREAD_LOCAL.get();
        return user == null ? null : user.getClientId();
    }

    public static String getProjectId() {
        LoginVal user = USER_INFO_THREAD_LOCAL.get();
        return user == null ? null : user.getProjectId();
    }


    public static void clear() {
        USER_INFO_THREAD_LOCAL.remove();
    }
}
