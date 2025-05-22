/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80012
 Source Host           : localhost:3306
 Source Schema         : ruoyi

 Target Server Type    : MySQL
 Target Server Version : 80012
 File Encoding         : 65001

 Date: 19/05/2025 20:34:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attendance_config
-- ----------------------------
DROP TABLE IF EXISTS `attendance_config`;
CREATE TABLE `attendance_config`  (
  `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `param_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '参数名',
  `param_value` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '参数值',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤规则字典' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of attendance_config
-- ----------------------------
INSERT INTO `attendance_config` VALUES (1, 'attendance_start_time', '06:00', 1, '上班考勤开始时间');
INSERT INTO `attendance_config` VALUES (2, 'attendance_time', '08:30', 1, '正常上班时间，超过此时间为迟到');
INSERT INTO `attendance_config` VALUES (3, 'attendance_end_time', '09:30', 1, '上班考勤截止时间');
INSERT INTO `attendance_config` VALUES (4, 'closing_start_time', '16:30', 1, '下班考勤开始时间');
INSERT INTO `attendance_config` VALUES (5, 'closing_time', '18:30', 1, '正常下班时间，早于此时间为早退');
INSERT INTO `attendance_config` VALUES (6, 'closing_end_time', '23:59', 1, '下班考勤截止时间');
INSERT INTO `attendance_config` VALUES (7, 'holiday_list', '[\"2025-01-01\",\"2025-02-04\",\"2025-02-05\",\"2025-02-06\",\"2025-04-05\",\"2025-05-01\",\"2025-06-22\",\"2025-09-29\",\"2025-10-01\",\"2025-10-02\",\"2025-10-03\"]', 1, '法定节假日列表');
INSERT INTO `attendance_config` VALUES (8, 'workday_list', '[\"2025-02-02\",\"2025-02-16\",\"2025-04-26\",\"2025-05-09\",\"2025-09-27\",\"2025-10-11\"]', 1, '节假日调休上班日列表');

-- ----------------------------
-- Table structure for attendance_info
-- ----------------------------
DROP TABLE IF EXISTS `attendance_info`;
CREATE TABLE `attendance_info`  (
  `attendance_info_id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `attendance_info_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '员工姓名',
  `emp_id` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '员工工号',
  `attendance_info_date` date NULL DEFAULT NULL COMMENT '考勤日期',
  `check_in_time` datetime NULL DEFAULT NULL COMMENT '上班打卡时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `check_out_time` datetime NULL DEFAULT NULL COMMENT '下班打卡时间',
  `check_in_status` bigint(20) NULL DEFAULT NULL COMMENT '上班打卡状态 (0-正常, 1-迟到, 3-缺勤)',
  `check_out_status` bigint(20) NULL DEFAULT NULL COMMENT '下班打卡状态 (0-正常, 2-早退, 3-缺勤)',
  `evaluate` int(11) NULL DEFAULT NULL COMMENT '评价类型（0优1良2中3差）',
  `go_longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '上班打卡经度',
  `go_latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '上班打卡纬度',
  `go_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '上班打卡地址',
  `out_longitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '下班打卡经度',
  `out_latitude` decimal(10, 6) NULL DEFAULT NULL COMMENT '下班打卡纬度',
  `out_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '下班打卡地址',
  PRIMARY KEY (`attendance_info_id`) USING BTREE,
  INDEX `FK_name`(`attendance_info_name` ASC) USING BTREE,
  INDEX `FK_attendance_id`(`emp_id` ASC) USING BTREE,
  CONSTRAINT `FK_attendance_id` FOREIGN KEY (`emp_id`) REFERENCES `emp_info` (`emp_info_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_name` FOREIGN KEY (`attendance_info_name`) REFERENCES `emp_info` (`emp_info_name`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 49 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '考勤管理表' ROW_FORMAT = DYNAMIC;

-- 此处省略部分内容，完整SQL文件请查看源代码

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '参数配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 注意：本文件仅包含基本结构和部分示例数据
-- 完整数据库脚本请见源文件
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;