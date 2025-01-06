/*
 Navicat Premium Data Transfer

 Source Server         : 喀医服务器
 Source Server Type    : MySQL
 Source Server Version : 80036
 Source Host           : 47.109.39.210:3306
 Source Schema         : company_score

 Target Server Type    : MySQL
 Target Server Version : 80036
 File Encoding         : 65001

 Date: 05/01/2025 17:31:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for average_sum
-- ----------------------------
DROP TABLE IF EXISTS `average_sum`;
CREATE TABLE `average_sum`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dimension_id` int(0) NULL DEFAULT NULL,
  `average_score` double NULL DEFAULT NULL COMMENT '平均分',
  `weight` double NULL DEFAULT NULL COMMENT '默认为1',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1: lxyz 2: 业务类型',
  `epoch` int(0) NULL DEFAULT NULL,
  `content` tinyint(0) NULL DEFAULT NULL COMMENT '级联选择器的内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 495 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for dimentions
-- ----------------------------
DROP TABLE IF EXISTS `dimentions`;
CREATE TABLE `dimentions`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '评分维度标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评分维度描述',
  `weight` double NULL DEFAULT NULL COMMENT '该维度所占权重',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of dimentions
-- ----------------------------
INSERT INTO `dimentions` VALUES (1, '思想上: 开放进取(O)', NULL, NULL);
INSERT INTO `dimentions` VALUES (2, '作风上: 把打胜仗作为信仰(W)', NULL, NULL);
INSERT INTO `dimentions` VALUES (3, '业务上: 创新求实(I)', NULL, NULL);
INSERT INTO `dimentions` VALUES (4, '工作中: 信任互助(H)', NULL, NULL);
INSERT INTO `dimentions` VALUES (5, '员工间: 尊重平等(R)', NULL, NULL);
INSERT INTO `dimentions` VALUES (6, '行为上: 业精于勤(D)', NULL, NULL);
INSERT INTO `dimentions` VALUES (7, '执行力: 使命必达(M)', NULL, NULL);
INSERT INTO `dimentions` VALUES (8, '团队上: 五湖四海(F)', NULL, NULL);
INSERT INTO `dimentions` VALUES (9, '成长上: 坚持自省(I)', NULL, NULL);

-- ----------------------------
-- Table structure for evaluate_relations
-- ----------------------------
DROP TABLE IF EXISTS `evaluate_relations`;
CREATE TABLE `evaluate_relations`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `evaluated_user` int(0) NULL DEFAULT NULL COMMENT '被评估人',
  `evaluator` int(0) NULL DEFAULT NULL COMMENT '评估人',
  `evaluate_type` tinyint(0) NULL DEFAULT NULL COMMENT '评估类型（固定评估、自主评估）',
  `epoch` int(0) NULL DEFAULT NULL COMMENT '所属轮次',
  `enable` int(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `evaluated_user`(`evaluated_user`) USING BTREE,
  INDEX `evaluator`(`evaluator`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 290 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for evaluate_table
-- ----------------------------
DROP TABLE IF EXISTS `evaluate_table`;
CREATE TABLE `evaluate_table`  (
  `meanings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文化九条释意',
  `principles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'assessment principles\r\n评估原则及要求',
  `opening_remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '其他卷首语',
  `title` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '成长评估标题'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluate_table
-- ----------------------------
INSERT INTO `evaluate_table` VALUES ('公司文化九条，要求思想上开放进取(O)，作风上把打胜仗作为信仰(W)，业务上创新求实(I)，工作中信任互助(T)，员工间尊重平等(R)，行为上业精于勤(D)，执行力使命必达(M)，团队上五湖四海(F)，成长上坚持自省(I)。本需求旨在设计一个成长评估系统，将上述文化九条作为评估依据，实现公司内部人员的交叉互评。', '请确保您的评价客观、公正，基于具体的工作表现和事例；避免使用模糊不清、可能引起误解或带有个人偏见的语言，确保沟通清晰、直接；鼓励给予正面的反馈，同时诚恳地指出改进的空间，帮助同事成长；尊重每位同事的独特个性与贡献，认识到每个人都是团队成功不可或缺的一部分。', '非常感谢您对公司第一次成长评估的积极参与和配合！成长评估是腾微公司“成长=贡献=回报 ”的践行基石 ，是诸位同仁以他人为镜 、坚持自省 ，不断成为更加优秀自己的具体执行 。\n公司的发展 ，依赖于每一位同仁持续成长 、不断提升， 当您在腾微大家庭中 ，不断成为更加优秀自己的时候 ，腾微也必将伴随您的成长 ，而不断从追赶 ，到超越 ，再到引领 。\n对于成长评估得分，公司不强调人与人比，而关注自己同自己过去比是否在持续提升 、不断前行 。\n前行需要榜样 ，每一位同仁都能看到公司个人平均得分， 以及个人最高得分 ，公司相信 ，在榜样的招领下，以及“成长=贡献=回报 ”的正向激发下，我们必将实现一帮优秀的人不断书写更加优秀的腾微！', '成长评估报告标题');

-- ----------------------------
-- Table structure for evaluation_process
-- ----------------------------
DROP TABLE IF EXISTS `evaluation_process`;
CREATE TABLE `evaluation_process`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评估流程步骤节点描述',
  `start_date` date NULL DEFAULT NULL COMMENT '起始日期',
  `end_date` date NULL DEFAULT NULL COMMENT '结束日期',
  `epoch` int(0) NULL DEFAULT NULL COMMENT '所属的评估轮次',
  `evaluate_step` tinyint(0) NULL DEFAULT NULL COMMENT '评估流程步骤节点',
  `enable` tinyint(0) NULL DEFAULT NULL COMMENT '1：处于该环节 0: 否',
  `extra` tinyint(0) NULL DEFAULT NULL COMMENT '得分看板对其他管理员是否可见,0：不可 1：可见',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 58 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for scores
-- ----------------------------
DROP TABLE IF EXISTS `scores`;
CREATE TABLE `scores`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dimension_id` int(0) NULL DEFAULT NULL,
  `score` double NULL DEFAULT NULL COMMENT '得分',
  `todo_id` bigint(0) NULL DEFAULT NULL COMMENT '某次评估的id',
  `weight` double NULL DEFAULT NULL COMMENT '某个纬度的权重',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '分数描述',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `dimension_id`(`dimension_id`) USING BTREE,
  INDEX `evaluate_task_id`(`todo_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2123 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for todo_list
-- ----------------------------
DROP TABLE IF EXISTS `todo_list`;
CREATE TABLE `todo_list`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `type` int(0) NULL DEFAULT NULL COMMENT '任务类型',
  `detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '任务详情',
  `present_date` datetime(0) NULL DEFAULT NULL COMMENT '代办创建时间',
  `operation` tinyint(0) NULL DEFAULT NULL COMMENT '操作（0：未完成评议；1：已完成评议;  2: 操作已无效（过期或主动进入了下一环节））',
  `reject_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '拒绝理由',
  `owner_id` int(0) NULL DEFAULT NULL COMMENT '所属人',
  `complete_time` datetime(0) NULL DEFAULT NULL COMMENT '代办完成时间',
  `evaluator_id` int(0) NULL DEFAULT NULL COMMENT '评估人id',
  `evaluated_id` int(0) NULL DEFAULT NULL COMMENT '被评估人id',
  `evaluator_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '评估人姓名',
  `evaluated_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '被评估人姓名',
  `enable` tinyint(0) NULL DEFAULT NULL COMMENT '打分确认，涉及到平均分的计算（管理员端）。 0：分数待确认 1：分数有效 2：分数无效（被驳回）',
  `epoch` int(0) NULL DEFAULT NULL COMMENT '代办所属轮次',
  `confidence_level` double NULL DEFAULT NULL COMMENT '置信度（评分看板） 默认为1.0',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名(工号)',
  `department` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone_number` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `hire_date` date NULL DEFAULT NULL COMMENT '入职时间',
  `resignation_date` date NULL DEFAULT NULL COMMENT '离职时间',
  `enabled` tinyint(0) NULL DEFAULT NULL COMMENT '账号是否被激活',
  `account_non_expired` tinyint(0) NULL DEFAULT NULL COMMENT '账户是否过期',
  `account_non_locked` tinyint(0) NULL DEFAULT NULL COMMENT '账户是否被锁定',
  `credentials_non_expired` tinyint(0) NULL DEFAULT NULL COMMENT '密码是否过期',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
  `supervisor_1` int(0) NULL DEFAULT NULL COMMENT '主管1id 0或null代表无主管',
  `supervisor_2` int(0) NULL DEFAULT NULL COMMENT '主管2id, 0或null代表无二级主管',
  `supervisor_3` int(0) NULL DEFAULT NULL COMMENT '主管3id, 0或null代表无二级主管',
  `supervisor_name_1` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管1姓名',
  `supervisor_name_2` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管2姓名',
  `supervisor_name_3` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管3姓名',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `role` tinyint(0) NULL DEFAULT NULL COMMENT '角色',
  `residence` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '常驻地',
  `work_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工号',
  `lxyz` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'LXYZ类型',
  `business_type` tinyint(0) NULL DEFAULT NULL COMMENT '业务类型',
  `is_delete` tinyint(0) NULL DEFAULT NULL COMMENT '是否被删除',
  `is_first_login` tinyint(0) NULL DEFAULT NULL COMMENT '是否首次登录',
  `hr` int(0) NULL DEFAULT NULL COMMENT 'hr',
  `weight` double NULL DEFAULT NULL COMMENT '得分看板的权重',
  `hr_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'HRBP',
  `weight1` double NULL DEFAULT NULL COMMENT '主管1权重',
  `weight2` double NULL DEFAULT NULL COMMENT '主管2权重',
  `weight3` double NULL DEFAULT NULL COMMENT '主管3权重',
  `first_admin` int(0) NULL DEFAULT NULL COMMENT '一级管理员(无则为0)',
  `second_admin` int(0) UNSIGNED NULL DEFAULT NULL COMMENT '二级管理员(无则为0)',
  `super_admin` int(0) NULL DEFAULT NULL COMMENT '超级管理员(无则为0)',
  `first_admin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `second_admin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `super_admin_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 333 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (214, 'Arvin001', '研发', '2540760998@qq.com', '13800000004', '00000000', '2020-04-01', NULL, 1, 1, 1, 1, '2025-01-02 16:47:43', '2025-01-05 16:56:16', 0, 0, 0, NULL, NULL, NULL, 'Arvin', 3, '深圳', '001', 'LP', 0, 0, 0, 0, 1, NULL, -1, -1, -1, 0, 0, 0, NULL, NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
