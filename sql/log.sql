-- =============================================================================
-- JYCloud 日志表
-- 适用数据库：MySQL 8.0+
-- 说明：本脚本只建表，不建库、不执行 DROP，可重复执行。
-- =============================================================================

CREATE TABLE IF NOT EXISTS t_login_log (
    id BIGINT NOT NULL COMMENT '主键（应用侧雪花ID）',
    tenant_id VARCHAR(64) NULL COMMENT '租户ID',
    user_id BIGINT NULL COMMENT '登录用户ID',
    username VARCHAR(128) NULL COMMENT '登录账号',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '登录结果：0失败 1成功',
    grant_type VARCHAR(64) NULL COMMENT '登录方式',
    description VARCHAR(500) NULL COMMENT '登录描述',
    login_time DATETIME NULL COMMENT '登录时间',
    ua VARCHAR(500) NULL COMMENT 'User-Agent',
    browser VARCHAR(100) NULL COMMENT '浏览器名称',
    browser_version VARCHAR(100) NULL COMMENT '浏览器版本',
    operating_system VARCHAR(100) NULL COMMENT '操作系统',
    location VARCHAR(255) NULL COMMENT '登录地点',
    ip VARCHAR(64) NULL COMMENT '登录IP',
    PRIMARY KEY (id),
    KEY idx_login_log_tenant_time (tenant_id, login_time),
    KEY idx_login_log_user_time (user_id, login_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci
  COMMENT = '登录日志';

CREATE TABLE IF NOT EXISTS t_log (
    id BIGINT NOT NULL COMMENT '主键（应用侧雪花ID）',
    tenant_id VARCHAR(64) NULL COMMENT '租户ID',
    service_id VARCHAR(128) NULL COMMENT '来源应用',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '操作结果：0失败 1成功',
    username VARCHAR(128) NULL COMMENT '操作账号',
    ip VARCHAR(64) NULL COMMENT '操作IP',
    description VARCHAR(500) NULL COMMENT '操作描述',
    class_path VARCHAR(512) NULL COMMENT '目标类',
    action_method VARCHAR(128) NULL COMMENT '目标方法',
    request_uri VARCHAR(1024) NULL COMMENT '请求地址',
    http_method VARCHAR(16) NULL COMMENT 'HTTP方法',
    params LONGTEXT NULL COMMENT '请求参数',
    result LONGTEXT NULL COMMENT '返回结果',
    ex_detail LONGTEXT NULL COMMENT '异常详情',
    start_time DATETIME(3) NULL COMMENT '开始时间',
    finish_time DATETIME(3) NULL COMMENT '完成时间',
    consuming_time BIGINT NULL COMMENT '耗时（毫秒）',
    ua VARCHAR(500) NULL COMMENT 'User-Agent',
    create_uid BIGINT NULL COMMENT '操作人ID',
    create_time DATETIME NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_log_tenant_time (tenant_id, create_time),
    KEY idx_log_user_time (create_uid, create_time),
    KEY idx_log_action (action_method, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci
  COMMENT = '系统操作日志';

CREATE TABLE IF NOT EXISTS t_api_log (
    id BIGINT NOT NULL COMMENT '主键（应用侧雪花ID）',
    request_id VARCHAR(64) NULL COMMENT '链路请求标识',
    client_id VARCHAR(64) NULL COMMENT '发起请求的应用ID',
    tenant_id VARCHAR(64) NULL COMMENT '租户ID',
    service_name VARCHAR(128) NULL COMMENT '提供API的服务名称',
    type TINYINT NOT NULL DEFAULT 1 COMMENT '调用结果：0失败 1成功',
    ip VARCHAR(64) NULL COMMENT '调用IP',
    action_id VARCHAR(128) NULL COMMENT 'API编号',
    description VARCHAR(500) NULL COMMENT 'API描述',
    class_path VARCHAR(512) NULL COMMENT '目标类',
    action_method VARCHAR(128) NULL COMMENT '目标方法',
    request_uri VARCHAR(1024) NULL COMMENT '请求地址',
    http_method VARCHAR(16) NULL COMMENT 'HTTP方法',
    params LONGTEXT NULL COMMENT '请求参数',
    response_code INT NULL COMMENT '响应状态码',
    response_msg VARCHAR(1000) NULL COMMENT '响应描述',
    response_data LONGTEXT NULL COMMENT '响应数据',
    ex_detail LONGTEXT NULL COMMENT '异常详情',
    start_time DATETIME(3) NULL COMMENT '开始时间',
    finish_time DATETIME(3) NULL COMMENT '完成时间',
    cost_time BIGINT NULL COMMENT '耗时（毫秒）',
    create_time DATETIME NULL COMMENT '创建时间',
    client_name VARCHAR(128) NULL COMMENT '应用名称',
    PRIMARY KEY (id),
    KEY idx_api_log_request (request_id),
    KEY idx_api_log_tenant_time (tenant_id, create_time),
    KEY idx_api_log_client_time (client_id, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci
  COMMENT = 'API调用日志';

CREATE TABLE IF NOT EXISTS t_log_error (
    id BIGINT NOT NULL COMMENT '主键（应用侧雪花ID）',
    tenant_id VARCHAR(64) NULL COMMENT '租户ID',
    service_id VARCHAR(128) NULL COMMENT '来源应用',
    username VARCHAR(128) NULL COMMENT '操作用户账号',
    ip VARCHAR(64) NULL COMMENT '操作IP',
    description VARCHAR(500) NULL COMMENT '错误描述',
    class_path VARCHAR(512) NULL COMMENT '异常类',
    action_method VARCHAR(128) NULL COMMENT '异常方法',
    request_uri VARCHAR(1024) NULL COMMENT '请求地址',
    http_method VARCHAR(16) NULL COMMENT 'HTTP方法',
    params LONGTEXT NULL COMMENT '请求参数',
    ex_detail LONGTEXT NULL COMMENT '异常堆栈',
    ua VARCHAR(500) NULL COMMENT 'User-Agent',
    create_uid BIGINT NULL COMMENT '操作用户ID',
    create_time DATETIME NULL COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_log_error_tenant_time (tenant_id, create_time),
    KEY idx_log_error_service_time (service_id, create_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci
  COMMENT = '系统错误日志';

