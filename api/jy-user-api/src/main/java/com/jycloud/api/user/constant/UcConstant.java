package com.jycloud.api.user.constant;

/**
 * uc常量
 */
public class UcConstant {

    // user Token缓存前缀
    public static final String USER_TOKEN_CACHE_PREFIX = "uc:cache:user:token:";
    // user缓存前缀
    public static final String USER_NAME_CACHE_PREFIX = "uc:cache:user:name:";
    public static final String USER_ID_CACHE_PREFIX = "uc:cache:user:id:";
    public static final String USER_MOBILE_CACHE_PREFIX = "uc:cache:user:mobile:";
    public static final String USER_ID_CARD_CACHE_PREFIX = "uc:cache:user:id_card:";
    public static final String USER_UNIONID_CACHE_PREFIX = "uc:cache:user:unionid:";

    // 用户在租户下的缓存前缀
    // user 部门缓存
    public static final String USER_DEPT_CACHE_PREFIX = "uc:cache:user:dept:";
    // user 数据权限缓存
    public static final String USER_DATA_PERMISSION_CACHE_PREFIX = "uc:cache:user:data:permission:";
    // user角色缓存前缀
    public static final String USER_ROLE_CACHE_PREFIX = "uc:cache:user:role:";
    // user关联的菜单缓存前缀
    public static final String USER_MENU_CACHE_PREFIX = "uc:cache:user:menu:";
    // user权限缓存前缀（权限标识集合）
    public static final String USER_PERMISSION_CACHE_PREFIX = "uc:cache:user:permission:";


    // 租户详情数据缓存前缀
    public static final String TENANT_INFO_CACHE_PREFIX ="uc:cache:tenant:info:";
    // 租户的部门数据缓存前缀
    public static final String TENANT_DEPT_CACHE_PREFIX = "uc:cache:tenant:dept:";
    // 租户的菜单缓存前缀
    public static final String TENANT_MENU_CACHE_PREFIX = "uc:cache:tenant:menu:";
    // 租户权限缓存版本号前缀（仅 Redis，不设 TTL，不入库）
    public static final String PERM_VERSION_CACHE_PREFIX = "perm:version:";

    // 系统应用数据缓存
    public static final String CLIENT_CACHE_PREFIX = "uc:cache:client:";
    // 系统验证码缓存前缀
    public static final String CAPTCHA_PREFIX = "uc:cache:captcha:";



    // 超級管理员账号
    public static final String SUPER_ADMIN_USERNAME = "admin";
    public static final String SUPER_ADMIN_ID = "1";

    // 用戶默认密码
    public static final String USER_PASSWORD = "123456";

    // 设置平台ID
    public static final String SETTING_CLIENT_ID = "1111";
    // 用户中心ID
    public static final String UCENTER_CLIENT_ID = "1112";


}
