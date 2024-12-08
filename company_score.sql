/*
 Navicat Premium Data Transfer

 Source Server         : company_score
 Source Server Type    : MySQL
 Source Server Version : 80028
 Source Host           : localhost:3306
 Source Schema         : company_score

 Target Server Type    : MySQL
 Target Server Version : 80028
 File Encoding         : 65001

 Date: 14/11/2024 17:36:18
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for average_sum
-- ----------------------------
DROP TABLE IF EXISTS `average_sum`;
CREATE TABLE `average_sum`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `dimention_id` int(0) NULL DEFAULT NULL,
  `average_score` double NULL DEFAULT NULL COMMENT '平均分',
  `weight` double NULL DEFAULT NULL COMMENT '默认为1',
  `type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '1: lxyz 2: 业务类型',
  `epoch` int(0) NULL DEFAULT NULL,
  `content` tinyint(0) NULL DEFAULT NULL COMMENT '级联选择器的内容',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

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
  `enable` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `evaluated_user`(`evaluated_user`) USING BTREE,
  INDEX `evaluator`(`evaluator`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 220 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluate_relations
-- ----------------------------
INSERT INTO `evaluate_relations` VALUES (111, 120, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (112, 122, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (113, 123, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (114, 124, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (115, 125, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (116, 126, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (117, 127, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (118, 128, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (119, 129, 130, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (120, 130, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (121, 121, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (122, 122, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (123, 123, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (124, 124, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (125, 125, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (126, 126, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (127, 127, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (128, 128, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (129, 129, 120, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (130, 130, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (131, 120, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (132, 122, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (133, 123, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (134, 124, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (135, 125, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (136, 126, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (137, 127, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (138, 128, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (139, 129, 121, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (140, 130, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (141, 120, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (142, 121, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (143, 123, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (144, 124, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (145, 125, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (146, 126, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (147, 127, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (148, 128, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (149, 129, 122, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (150, 130, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (151, 120, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (152, 121, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (153, 122, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (154, 124, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (155, 125, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (156, 126, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (157, 127, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (158, 128, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (159, 129, 123, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (160, 130, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (161, 120, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (162, 121, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (163, 122, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (164, 123, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (165, 125, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (166, 126, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (167, 127, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (168, 128, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (169, 129, 124, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (170, 130, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (171, 120, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (172, 121, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (173, 122, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (174, 123, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (175, 124, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (176, 126, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (177, 127, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (178, 128, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (179, 129, 125, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (180, 130, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (181, 120, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (182, 121, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (183, 122, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (184, 123, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (185, 124, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (186, 125, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (187, 127, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (188, 128, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (189, 129, 126, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (190, 130, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (191, 120, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (192, 121, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (193, 122, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (194, 123, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (195, 124, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (196, 125, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (197, 126, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (198, 128, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (199, 129, 127, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (200, 130, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (201, 120, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (202, 121, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (203, 122, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (204, 123, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (205, 124, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (206, 125, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (207, 126, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (208, 127, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (209, 129, 128, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (210, 130, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (211, 120, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (212, 121, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (213, 122, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (214, 123, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (215, 124, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (216, 125, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (217, 126, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (218, 127, 129, 1, 2, '1');
INSERT INTO `evaluate_relations` VALUES (219, 128, 129, 1, 2, '1');

-- ----------------------------
-- Table structure for evaluate_table
-- ----------------------------
DROP TABLE IF EXISTS `evaluate_table`;
CREATE TABLE `evaluate_table`  (
  `meanings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '文化九条释意',
  `principles` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'assessment principles\r\n评估原则及要求',
  `opening_remarks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '其他卷首语'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluate_table
-- ----------------------------
INSERT INTO `evaluate_table` VALUES ('公司文化九条，要求思想上开放进取(O)，作风上把打胜仗作为信仰(W)，业务上创新求实(I)，工作中信任互助(H)，员工间尊重平等(R)，行为上业精于勤(D)，执行力使命必达(M)，团队上五湖四海(F)，成长上坚持自省(I)。本需求旨在设计一个成长评估系统，将上述文化九条作为评估依据，实现公司内部人员的交叉互评。', '请确保您的评价客观、公正，基于具体的工作表现和事例；避免使用模糊不清、可能引起误解或带有个人偏见的语言，确保沟通清晰、直接；鼓励给予正面的反馈，同时诚恳地指出改进的空间，帮助同事成长；尊重每位同事的独特个性与贡献，认识到每个人都是团队成功不可或缺的一部分。', '这里是需要在卷首展示的其他信息...');

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
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of evaluation_process
-- ----------------------------
INSERT INTO `evaluation_process` VALUES (9, '建立评估矩阵关系', '2020-03-02', '2020-03-10', 1, 1, 0);
INSERT INTO `evaluation_process` VALUES (10, '上级评估/周边评估', '2020-04-02', '2020-04-10', 1, 2, 0);
INSERT INTO `evaluation_process` VALUES (11, '结果沟通', '2020-05-02', '2020-05-10', 1, 3, 0);
INSERT INTO `evaluation_process` VALUES (12, '结果发布', '2020-05-02', '2020-05-10', 1, 4, 0);
INSERT INTO `evaluation_process` VALUES (13, '建立评估矩阵关系', '2020-03-02', '2020-03-10', 2, 1, 0);
INSERT INTO `evaluation_process` VALUES (14, '上级评估/周边评估', '2020-04-02', '2020-04-10', 2, 2, 1);
INSERT INTO `evaluation_process` VALUES (15, '结果沟通', '2020-05-02', '2020-05-10', 2, 3, 0);
INSERT INTO `evaluation_process` VALUES (16, '结果发布', '2020-05-02', '2020-05-20', 2, 4, 0);

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
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

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
) ENGINE = InnoDB AUTO_INCREMENT = 439 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of todo_list
-- ----------------------------
INSERT INTO `todo_list` VALUES (330, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 130, 120, '小1', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (331, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 130, 122, '小1', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (332, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 130, 123, '小1', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (333, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 130, 124, '小1', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (334, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 130, 125, '小1', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (335, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 130, 126, '小1', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (336, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 130, 127, '小1', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (337, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 130, 128, '小1', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (338, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 130, 129, '小1', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (339, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 120, 130, '小2', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (340, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 120, 121, '小2', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (341, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 120, 122, '小2', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (342, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 120, 123, '小2', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (343, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 120, 124, '小2', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (344, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 120, 125, '小2', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (345, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 120, 126, '小2', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (346, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 120, 127, '小2', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (347, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 120, 128, '小2', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (348, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 120, 129, '小2', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (349, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 121, 130, '小3', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (350, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 121, 120, '小3', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (351, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 121, 122, '小3', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (352, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 121, 123, '小3', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (353, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 121, 124, '小3', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (354, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 121, 125, '小3', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (355, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 121, 126, '小3', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (356, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 121, 127, '小3', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (357, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 121, 128, '小3', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (358, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 121, 129, '小3', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (359, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 122, 130, '小4', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (360, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 122, 120, '小4', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (361, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 122, 121, '小4', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (362, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 122, 123, '小4', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (363, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 122, 124, '小4', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (364, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 122, 125, '小4', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (365, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 122, 126, '小4', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (366, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 122, 127, '小4', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (367, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 122, 128, '小4', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (368, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 122, 129, '小4', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (369, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 123, 130, '小5', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (370, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 123, 120, '小5', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (371, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 123, 121, '小5', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (372, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 123, 122, '小5', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (373, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 123, 124, '小5', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (374, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 123, 125, '小5', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (375, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 123, 126, '小5', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (376, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 123, 127, '小5', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (377, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 123, 128, '小5', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (378, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 123, 129, '小5', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (379, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 124, 130, '小6', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (380, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 124, 120, '小6', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (381, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 124, 121, '小6', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (382, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 124, 122, '小6', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (383, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 124, 123, '小6', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (384, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 124, 125, '小6', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (385, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 124, 126, '小6', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (386, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 124, 127, '小6', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (387, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 124, 128, '小6', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (388, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 124, 129, '小6', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (389, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 125, 130, '小7', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (390, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 125, 120, '小7', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (391, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 125, 121, '小7', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (392, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 125, 122, '小7', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (393, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 125, 123, '小7', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (394, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 125, 124, '小7', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (395, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 125, 126, '小7', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (396, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 125, 127, '小7', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (397, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 125, 128, '小7', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (398, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 125, 129, '小7', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (399, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 126, 130, '小8', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (400, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 126, 120, '小8', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (401, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 126, 121, '小8', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (402, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 126, 122, '小8', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (403, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 126, 123, '小8', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (404, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 126, 124, '小8', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (405, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 126, 125, '小8', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (406, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 126, 127, '小8', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (407, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 126, 128, '小8', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (408, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 126, 129, '小8', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (409, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 127, 130, '小9', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (410, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 127, 120, '小9', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (411, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 127, 121, '小9', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (412, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 127, 122, '小9', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (413, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 127, 123, '小9', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (414, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 127, 124, '小9', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (415, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 127, 125, '小9', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (416, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 127, 126, '小9', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (417, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 127, 128, '小9', '小10', 0, 2, 1);
INSERT INTO `todo_list` VALUES (418, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 127, 129, '小9', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (419, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 128, 130, '小10', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (420, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 128, 120, '小10', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (421, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 128, 121, '小10', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (422, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 128, 122, '小10', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (423, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 128, 123, '小10', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (424, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 128, 124, '小10', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (425, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 128, 125, '小10', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (426, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 128, 126, '小10', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (427, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 128, 127, '小10', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (428, 1, '请完成对小11的周边评议', '2024-11-14 14:53:33', 0, NULL, 129, NULL, 128, 129, '小10', '小11', 0, 2, 1);
INSERT INTO `todo_list` VALUES (429, 1, '请完成对小1的周边评议', '2024-11-14 14:53:33', 0, NULL, 130, NULL, 129, 130, '小11', '小1', 0, 2, 1);
INSERT INTO `todo_list` VALUES (430, 1, '请完成对小2的周边评议', '2024-11-14 14:53:33', 0, NULL, 120, NULL, 129, 120, '小11', '小2', 0, 2, 1);
INSERT INTO `todo_list` VALUES (431, 1, '请完成对小3的周边评议', '2024-11-14 14:53:33', 0, NULL, 121, NULL, 129, 121, '小11', '小3', 0, 2, 1);
INSERT INTO `todo_list` VALUES (432, 1, '请完成对小4的周边评议', '2024-11-14 14:53:33', 0, NULL, 122, NULL, 129, 122, '小11', '小4', 0, 2, 1);
INSERT INTO `todo_list` VALUES (433, 1, '请完成对小5的周边评议', '2024-11-14 14:53:33', 0, NULL, 123, NULL, 129, 123, '小11', '小5', 0, 2, 1);
INSERT INTO `todo_list` VALUES (434, 1, '请完成对小6的周边评议', '2024-11-14 14:53:33', 0, NULL, 124, NULL, 129, 124, '小11', '小6', 0, 2, 1);
INSERT INTO `todo_list` VALUES (435, 1, '请完成对小7的周边评议', '2024-11-14 14:53:33', 0, NULL, 125, NULL, 129, 125, '小11', '小7', 0, 2, 1);
INSERT INTO `todo_list` VALUES (436, 1, '请完成对小8的周边评议', '2024-11-14 14:53:33', 0, NULL, 126, NULL, 129, 126, '小11', '小8', 0, 2, 1);
INSERT INTO `todo_list` VALUES (437, 1, '请完成对小9的周边评议', '2024-11-14 14:53:33', 0, NULL, 127, NULL, 129, 127, '小11', '小9', 0, 2, 1);
INSERT INTO `todo_list` VALUES (438, 1, '请完成对小10的周边评议', '2024-11-14 14:53:33', 0, NULL, 128, NULL, 129, 128, '小11', '小10', 0, 2, 1);

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
  `supervisor_4` int(0) NULL DEFAULT NULL COMMENT '主管4id, 0或null代表无二级主管',
  `supervisor_name_1` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管1姓名',
  `supervisor_name_2` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管2姓名',
  `supervisor_name_3` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管3姓名',
  `supervisor_name_4` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '主管4姓名',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `role` tinyint(0) NULL DEFAULT NULL COMMENT '角色',
  `residence` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '常驻地',
  `work_num` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工号',
  `lxyz` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'LXYZ类型',
  `business_type` tinyint(0) NULL DEFAULT NULL COMMENT '业务类型',
  `is_delete` tinyint(0) NULL DEFAULT NULL COMMENT '是否被删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 131 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (120, '小2', '研发', '2339134840@qq.com', '13800000001', '123456', '2020-01-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '', '', '', '', '小2', 3, '北京', '001', 'IP', 0, 0);
INSERT INTO `user` VALUES (121, '小3', '市场', '2339134840@qq.com', '13800000002', '123456', '2020-02-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小7', '', '', '', '小3', 1, '上海', '002', 'LP', 1, 0);
INSERT INTO `user` VALUES (122, '小4', '研发', '2339134840@qq.com', '13800000003', '123456', '2020-03-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小2', '', '', '', '小4', 0, '广州', '003', 'LP', 0, 0);
INSERT INTO `user` VALUES (123, '小5', '研发', '2339134840@qq.com', '13800000004', '123456', '2020-04-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小2', '', '', '', '小5', 0, '深圳', '004', 'LP', 0, 0);
INSERT INTO `user` VALUES (124, '小6', '研发', '2339134840@qq.com', '13800000005', '123456', '2020-05-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小2', '', '', '', '小6', 0, '杭州', '005', '中坚', 0, 0);
INSERT INTO `user` VALUES (125, '小7', '市场', '2339134840@qq.com', '13800000006', '123456', '2020-06-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '', '', '', '', '小7', 1, '成都', '006', '中坚', 0, 0);
INSERT INTO `user` VALUES (126, '小8', '研发', '2339134840@qq.com', '13800000007', '123456', '2020-07-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小2', '', '', '', '小8', 0, '武汉', '007', '成长', 0, 0);
INSERT INTO `user` VALUES (127, '小9', '市场', '2339134840@qq.com', '13800000008', '123456', '2020-08-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小7', '', '', '', '小9', 0, '西安', '008', '精英', 1, 0);
INSERT INTO `user` VALUES (128, '小10', '市场', '2339134840@qq.com', '13800000009', '123456', '2020-09-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '小7', '小2', '', '', '小10', 0, '北京', '009', '精英', 1, 0);
INSERT INTO `user` VALUES (129, '小11', '市场', '2339134840@qq.com', '13800000010', '123456', '2020-10-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '', '', '', '', '小11', 2, '上海', '010', '精英', 1, 0);
INSERT INTO `user` VALUES (130, '小1', '市场', '2339134840@qq.com', '13800000010', '123456', '2020-10-01', NULL, 1, 1, 1, 1, '2024-11-14 14:32:47', '2024-11-14 14:52:58', NULL, NULL, NULL, NULL, '', '', '', '', '小1', 2, '上海', '1010.0', '精英', 1, 0);

SET FOREIGN_KEY_CHECKS = 1;
