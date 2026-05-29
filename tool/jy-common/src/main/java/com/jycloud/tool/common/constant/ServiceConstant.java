package com.jycloud.tool.common.constant;


public interface ServiceConstant {

    /**
     * 应用版本
     */
    String APPLICATION_VERSION = "1.0";

    /**
     * 管理员接口,开发平台使用
     */
    String ADMIN_PATH_PREFIX = "admin";
    /**
     * 系统接口前缀,用于vue后台使用
     */
    String SYSTEM_PATH_PREFIX = "system";
    /**
     * 内部系统接口,后端服务之间使用
     */
    String CLIENT_PATH_PREFIX = "client";
    /**
     * 外部系统接口
     */
    String API_PATH_PREFIX = "api";

    /**
     * 应用名前缀
     */
    String APPLICATION_NAME_PREFIX = "jg-";

    /**
     * 网关模块名称
     */
    String GATEWAY_NAME = APPLICATION_NAME_PREFIX + "gateway";

    /**
     * 授权模块名称
     */
    String AUTH_NAME = APPLICATION_NAME_PREFIX + "auth";

    /**
     * 用户模块
     */
    String USER_NAME = APPLICATION_NAME_PREFIX + "user";

    /**
     * 用户中心
     */
    String UCENTER_NAME = APPLICATION_NAME_PREFIX + "ucenter";

    /**
     * 合同模块
     */
    String CONTRACT_NAME = APPLICATION_NAME_PREFIX + "contract";

    /**
     * 设置平台
     */
    String SETTING_NAME = APPLICATION_NAME_PREFIX + "setting";

    /**
     * 对象存储
     */
    String OSS_NAME = APPLICATION_NAME_PREFIX + "oss";

    /**
     * 采集平台
     */
    String GATHER_NAME = APPLICATION_NAME_PREFIX + "gather";

    /**
     * 360VR平台
     */
    String VR_WEB_NAME = APPLICATION_NAME_PREFIX + "360vr-web";

    /**
     * 360VR 服务
     */
    String VR_Serve_NAME = APPLICATION_NAME_PREFIX + "360vr-serve";

    /**
     *  visual 可视化服务
     */
    String VISUAL_NAME = APPLICATION_NAME_PREFIX + "visual";

    /**
     * business-setting 作业管理设置平台
     */
    String BUSINESS_SETTING_NAME = APPLICATION_NAME_PREFIX + "business-setting";

    /**
     * business-web 作业管理平台
     */
    String BUSINESS_WEB_NAME = APPLICATION_NAME_PREFIX + "business-web";

    /**
     * housing-ticket-setting 房票管理
     */
    String HOUSING_TICKET_SETTING_NAME = APPLICATION_NAME_PREFIX + "housing-ticket-setting";
    /**
     * housing-ticket-setting 房票管理
     */
    String HOUSING_TICKET_WEB_NAME = APPLICATION_NAME_PREFIX + "housing-ticket-web";


}
