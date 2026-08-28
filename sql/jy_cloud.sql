/*
 Navicat Premium Data Transfer

 Source Server         : 武汉测试环境-120.27.143.176
 Source Server Type    : MySQL
 Source Server Version : 80040
 Source Host           : 120.27.143.176:3306
 Source Schema         : jy_cloud

 Target Server Type    : MySQL
 Target Server Version : 80040
 File Encoding         : 65001

 Date: 21/05/2026 09:46:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_client
-- ----------------------------
DROP TABLE IF EXISTS `t_client`;
CREATE TABLE `t_client`  (
  `id` bigint(0) UNSIGNED NOT NULL AUTO_INCREMENT,
  `client_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID',
  `parent_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '0' COMMENT '父级应用ID',
  `category` int(0) NULL DEFAULT NULL COMMENT '类型,0内部系统,1业务系统, 2外部系统',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `app_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务系统名',
  `app_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'appkey',
  `app_secret` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '秘钥',
  `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '盐',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '业务系统简介',
  `website` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '网站地址',
  `legal_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '合法IP,逗号分割',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态',
  `create_uid` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_client_id`(`client_id`) USING BTREE,
  INDEX `idx_client_app_status`(`client_id`, `app_key`, `status`) USING BTREE,
  INDEX `idx_app_key_status`(`app_key`, `status`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '业务系统表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_client_versions
-- ----------------------------
DROP TABLE IF EXISTS `t_client_versions`;
CREATE TABLE `t_client_versions`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID',
  `version` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '版本',
  `description` varchar(350) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `release_date` datetime(0) NOT NULL COMMENT '发布时间',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态',
  `create_uid` bigint(0) NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_client_status_del`(`client_id`, `status`, `delete_flag`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '应用版本管理记录' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dept
-- ----------------------------
DROP TABLE IF EXISTS `t_dept`;
CREATE TABLE `t_dept`  (
  `dept_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint(0) NOT NULL COMMENT '上级部门ID',
  `dept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '部门名称',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态 0锁定 1有效',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  `create_uid` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`dept_id`) USING BTREE,
  INDEX `idx_parent_tenant_status`(`parent_id`, `tenant_id`, `status`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE,
  INDEX `idx_tenant_status`(`tenant_id`, `status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_dict`;
CREATE TABLE `t_dict`  (
  `dict_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint(0) NULL DEFAULT 0 COMMENT '父主键',
  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典码',
  `dict_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典值',
  `dict_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字典名称',
  `sort` int(0) NULL DEFAULT NULL COMMENT '排序',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字典备注',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态',
  `create_uid` bigint(0) NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`dict_id`) USING BTREE,
  INDEX `idx_code_key`(`code`, `dict_key`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '字典表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_menu`;
CREATE TABLE `t_menu`  (
  `menu_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '菜单/按钮ID',
  `client_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID',
  `parent_id` bigint(0) NOT NULL DEFAULT 0 COMMENT '上级菜单ID',
  `menu_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单/按钮名称Title',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单/按钮名称',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '对应路由path',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '对应路由组件component',
  `perms` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `category` tinyint(1) NOT NULL COMMENT '类型 0菜单 1按钮',
  `hide` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏;1隐藏0不隐藏',
  `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态',
  `create_uid` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`menu_id`) USING BTREE,
  INDEX `idx_category_parent_client`(`category`, `parent_id`, `client_id`) USING BTREE,
  INDEX `idx_client_id`(`client_id`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_project_client
-- ----------------------------
DROP TABLE IF EXISTS `t_project_client`;
CREATE TABLE `t_project_client`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` bigint(0) NOT NULL COMMENT '项目ID',
  `client_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  `create_uid` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_project_client`(`tenant_id`, `project_id`, `client_id`) USING BTREE,
  INDEX `idx_tenant_project`(`tenant_id`, `project_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '项目应用配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_region
-- ----------------------------
DROP TABLE IF EXISTS `t_region`;
CREATE TABLE `t_region`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `code_id` int(0) NOT NULL DEFAULT 0 COMMENT '邮编',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标志',
  `fullname` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '城市全称',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `pid` int(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '上级分类ID',
  `pid_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '层级路径',
  `code_pid` int(0) NOT NULL DEFAULT 0 COMMENT '邮编父级',
  `level` tinyint(1) NOT NULL DEFAULT 0 COMMENT '1省2市3区',
  `sort` int(0) UNSIGNED NULL DEFAULT 0 COMMENT '排序（同级有效）',
  `status` tinyint(1) UNSIGNED NULL DEFAULT 1 COMMENT '数据状态(1默认)',
  `note` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `lat` decimal(10, 7) NULL DEFAULT NULL COMMENT '纬度',
  `lon` decimal(10, 7) NULL DEFAULT NULL COMMENT '经度',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_pid`(`pid`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_code_id`(`code_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`  (
  `role_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `sort` int(0) NULL DEFAULT 0 COMMENT '排序',
  `readonly` tinyint(1) NULL DEFAULT NULL COMMENT '内置角色(0不是,1是)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  `create_uid` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`role_id`) USING BTREE,
  INDEX `idx_tenant_readonly_name`(`tenant_id`, `readonly`, `role_name`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `t_role_menu`;
CREATE TABLE `t_role_menu`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `menu_id` bigint(0) NULL DEFAULT NULL COMMENT '菜单id',
  `role_id` bigint(0) NULL DEFAULT NULL COMMENT '角色id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_menu`(`role_id`, `menu_id`) USING BTREE,
  INDEX `idx_menu_id`(`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_tenant
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant`;
CREATE TABLE `t_tenant`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  `tenant_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户名称',
  `domain` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '域名地址',
  `linkman` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_tel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系地址',
  `status` tinyint(1) NULL DEFAULT NULL COMMENT '状态',
  `create_uid` bigint(0) NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_status_del`(`tenant_id`, `status`, `delete_flag`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_tenant_client
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_client`;
CREATE TABLE `t_tenant_client`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `client_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '应用ID',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  `create_uid` bigint(0) NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_client_del`(`tenant_id`, `client_id`, `delete_flag`) USING BTREE,
  INDEX `idx_tenant_client_status_del`(`tenant_id`, `client_id`, `status`, `delete_flag`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '租户授权的应用表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`  (
  `user_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `unionid` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `salt` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码盐值',
  `realname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `nickname` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户头像',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `mobile` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `id_card_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '身份证号码',
  `ssex` tinyint(1) NOT NULL DEFAULT 0 COMMENT '0保密1男2女',
  `birthday` date NULL DEFAULT NULL COMMENT '生日',
  `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `dept_id` bigint(0) NULL DEFAULT 0 COMMENT '部门ID',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最后登录ip地址',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态',
  `create_uid` bigint(0) NOT NULL DEFAULT 0 COMMENT '创建人ID',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_uid` bigint(0) NULL DEFAULT 0 COMMENT '更新人ID',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delete_flag` int(0) NULL DEFAULT 0 COMMENT '删除标记 0有效,1删除',
  `delete_time` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  `delete_uid` bigint(0) NULL DEFAULT NULL COMMENT '删除人ID',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `idx_username_del`(`username`, `delete_flag`) USING BTREE,
  INDEX `idx_mobile_del`(`mobile`, `delete_flag`) USING BTREE,
  INDEX `idx_unionid_del`(`unionid`, `delete_flag`) USING BTREE,
  INDEX `idx_realname`(`realname`) USING BTREE,
  INDEX `idx_status_del`(`status`, `delete_flag`) USING BTREE,
  INDEX `idx_delete_time`(`delete_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for t_user_authority
-- ----------------------------
DROP TABLE IF EXISTS `t_user_authority`;
CREATE TABLE `t_user_authority`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category` tinyint(1) NOT NULL COMMENT '类型 0角色(role_id),1资源(menu_id)',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `data_id` bigint(0) NULL DEFAULT NULL COMMENT '数据id',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_tenant_category`(`user_id`, `tenant_id`, `category`) USING BTREE,
  INDEX `idx_data_category`(`data_id`, `category`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_data_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_user_data_permission`;
CREATE TABLE `t_user_data_permission`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `data_scope` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '数据权限范围： all（全部）、dept（部门）、myself（自己）',
  `dept_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '部门ID',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_tenant`(`user_id`, `tenant_id`) USING BTREE,
  INDEX `idx_dept_id`(`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户数据权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `t_user_dept`;
CREATE TABLE `t_user_dept`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dept_id` bigint(0) NOT NULL COMMENT '部门ID',
  `user_id` bigint(0) NOT NULL COMMENT '用户id',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_tenant`(`user_id`, `tenant_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户部门表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for t_user_tenant
-- ----------------------------
DROP TABLE IF EXISTS `t_user_tenant`;
CREATE TABLE `t_user_tenant`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tenant_id` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '租户ID',
  `user_id` bigint(0) NULL DEFAULT NULL COMMENT '用户id',
  `if_default` tinyint(0) NULL DEFAULT 0 COMMENT '是否是默认的租户: 0不是,1是',
  `consumer` tinyint(1) NULL DEFAULT 0 COMMENT '是否是C端用户: 0不是,1是',
  `manager` tinyint(1) NULL DEFAULT 1 COMMENT '是否是B端用户: 0不是,1是',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态:0禁用 1正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tenant_user`(`tenant_id`, `user_id`) USING BTREE COMMENT '唯一索引',
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户租户表' ROW_FORMAT = DYNAMIC;

SET FOREIGN_KEY_CHECKS = 1;
