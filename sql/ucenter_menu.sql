-- ============================================================
-- 用户中心（clientId 1112）初始化菜单
-- 在 jy_cloud 库中执行；菜单 ID 由自增主键生成
-- 组件路径对应前端 src/pages 目录：
--   user-center/user/index -> src/pages/user-center/user/index.vue
--   user-center/role/index -> src/pages/user-center/role/index.vue
--   user-center/dept/index -> src/pages/user-center/dept/index.vue
-- 个人中心/修改密码 不进菜单，在右上角用户名下拉中弹窗展示
-- ============================================================
INSERT INTO `t_menu`
  (`client_id`, `parent_id`, `menu_title`, `menu_name`, `path`, `component`, `perms`, `icon`, `category`, `hide`, `sort`, `status`, `create_uid`, `create_time`, `update_uid`, `update_time`, `delete_flag`)
VALUES
  ('1112', 0, '用户管理', 'UcenterUser', '/user-center/user', 'user-center/user/index', NULL, 'user-circle', 0, 0, 1, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 0, '角色管理', 'UcenterRole', '/user-center/role', 'user-center/role/index', NULL, 'secure', 0, 0, 2, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 0, '部门管理', 'UcenterDept', '/user-center/dept', 'user-center/dept/index', NULL, 'group', 0, 0, 3, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 1, '新增用户', '', '', NULL, NULL, 'jy-ucenter:user:create', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 1, '编辑用户', '', '', NULL, NULL, 'jy-ucenter:user:update', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 1, '删除用户', '', '', NULL, NULL, 'jy-ucenter:user:delete', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 1, '重置密码', '', '', NULL, NULL, 'jy-ucenter:user:resetPassword', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 1, '分配角色', '', '', NULL, NULL, 'jy-ucenter:user-authority:set', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 2, '新增角色', '', '', NULL, NULL, 'jy-ucenter:role:create', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 2, '编辑角色', '', '', NULL, NULL, 'jy-ucenter:role:update', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 2, '删除角色', '', '', NULL, NULL, 'jy-ucenter:role:delete', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 3, '新增部门', '', '', NULL, NULL, 'jy-ucenter:dept:create', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 3, '编辑部门', '', '', NULL, NULL, 'jy-ucenter:dept:update', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0),
  ('1112', 3, '删除部门', '', '', NULL, NULL, 'jy-ucenter:dept:delete', NULL, 1, 0, 0, 1, 1, NOW(), 1, NOW(), 0);