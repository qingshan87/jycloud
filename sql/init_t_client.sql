-- =====================================================
-- 初始化内置平台数据：设置平台(1111)、用户中心(1112)
-- 说明：app_key 由 AppUtil.generateAppKey() 生成，
--       salt 为随机盐，app_secret = MD5(app_key + salt) 迭代2次
-- =====================================================

INSERT INTO t_client
    (client_id, parent_id, category, icon, app_name, app_key, app_secret, salt, description, website, legal_ip, status, create_time, update_time, delete_flag)
VALUES
    ('1111', NULL, 0, NULL, '设置平台', 'umlZO2IN', '3d8a8584e4b81a3a35c42950f4cd215d', 'f85db4d40661f182', '系统设置平台', NULL, NULL, 1, NOW(), NOW(), 0),
    ('1112', NULL, 0, NULL, '用户中心', '6c7FQRl4', '4927b2ac839377ec27cf1da80f76690e', '7574031adb07823b', '用户中心平台', NULL, NULL, 1, NOW(), NOW(), 0);
