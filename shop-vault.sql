SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 用户表 (sys_user)
-- 支持游客注册、会员管理、余额与积分
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '加密密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `balance` decimal(10,2) DEFAULT '0.00' COMMENT '钱包余额(模拟支付用)',
  `points` int(11) DEFAULT '0' COMMENT '当前积分',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 1:正常 0:冻结',
  `role` varchar(20) DEFAULT 'USER' COMMENT '角色: USER/ADMIN',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 收货地址表 (sys_address)
-- ----------------------------
DROP TABLE IF EXISTS `sys_address`;
CREATE TABLE `sys_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(64) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) NOT NULL COMMENT '收货人电话',
  `province` varchar(64) DEFAULT NULL COMMENT '省',
  `city` varchar(64) DEFAULT NULL COMMENT '市',
  `region` varchar(64) DEFAULT NULL COMMENT '区',
  `detail_address` varchar(255) NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收货地址表';

-- ----------------------------
-- 3. 商品分类表 (pms_category)
-- ----------------------------
DROP TABLE IF EXISTS `pms_category`;
CREATE TABLE `pms_category` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '分类名称 (如: 数码, 服装)',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父分类ID (0为一级分类)',
  `level` int(1) DEFAULT '1' COMMENT '层级 (1:一级 2:二级)',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标URL',
  `sort` int(11) DEFAULT '0' COMMENT '排序权重',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ----------------------------
-- 4. YOLO视觉映射表 (sys_yolo_mapping)
-- [核心创新点]: 将YOLO识别出的英文标签映射到系统的商品分类
-- ----------------------------
DROP TABLE IF EXISTS `sys_yolo_mapping`;
CREATE TABLE `sys_yolo_mapping` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `yolo_label` varchar(64) NOT NULL COMMENT 'YOLO模型输出的标签 (如: cup, backpack)',
  `category_id` bigint(20) NOT NULL COMMENT '关联的系统分类ID',
  `confidence_threshold` decimal(4,2) DEFAULT '0.50' COMMENT '置信度阈值 (过滤低可信度识别)',
  `is_active` tinyint(1) DEFAULT '1' COMMENT '是否启用映射',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_yolo` (`yolo_label`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI视觉标签映射表';

-- ----------------------------
-- 5. 商品信息表 (pms_product)
-- ----------------------------
DROP TABLE IF EXISTS `pms_product`;
CREATE TABLE `pms_product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_id` bigint(20) NOT NULL COMMENT '分类ID',
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `sub_title` varchar(255) DEFAULT NULL COMMENT '副标题/卖点',
  `main_image` varchar(500) NOT NULL COMMENT '主图',
  `price` decimal(10,2) NOT NULL COMMENT '销售价格',
  `stock` int(11) NOT NULL COMMENT '库存数量',
  `stock_warning` int(11) DEFAULT '10' COMMENT '库存预警阈值',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 1:上架 0:下架',
  `sales` int(11) DEFAULT '0' COMMENT '销量 (用于热销推荐)',
  `detail_html` text COMMENT '商品详情(富文本)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ----------------------------
-- 6. 购物车表 (oms_cart_item)
-- ----------------------------
DROP TABLE IF EXISTS `oms_cart_item`;
CREATE TABLE `oms_cart_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `quantity` int(11) DEFAULT '1' COMMENT '购买数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ----------------------------
-- 7. 订单主表 (oms_order)
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) NOT NULL COMMENT '订单编号(唯一)',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10,2) NOT NULL COMMENT '订单总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额 (扣除优惠后)',
  `status` int(2) DEFAULT '0' COMMENT '状态: 0待付款 1待发货 2已发货 3已完成 4已关闭 5售后中',
  `receiver_snapshot` text COMMENT '收货人信息快照(JSON格式，防止地址修改影响旧订单)',
  `tracking_company` varchar(64) DEFAULT NULL COMMENT '物流公司',
  `tracking_no` varchar(64) DEFAULT NULL COMMENT '物流单号',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '发货时间',
  `receive_time` datetime DEFAULT NULL COMMENT '确认收货时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- ----------------------------
-- 8. 订单明细表 (oms_order_item)
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `product_id` bigint(20) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_img` varchar(500) DEFAULT NULL,
  `product_price` decimal(10,2) NOT NULL COMMENT '购买时的单价',
  `quantity` int(11) DEFAULT '1' COMMENT '购买数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单商品详情表';

-- ----------------------------
-- 9. 积分记录表 (sms_points_record)
-- [核心创新点]: 实现“交易+会员”闭环，记录积分的获取与消耗
-- ----------------------------
DROP TABLE IF EXISTS `sms_points_record`;
CREATE TABLE `sms_points_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `type` tinyint(1) NOT NULL COMMENT '类型: 1签到 2购物奖励 3兑换消耗 4活动赠送',
  `amount` int(11) NOT NULL COMMENT '变动数量 (正数为增，负数为减)',
  `description` varchar(255) DEFAULT NULL COMMENT '描述 (如: 2026/xx/xx 每日签到)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分变动记录表';

-- ----------------------------
-- 10. 会员活动/积分商城表 (sms_activity)
-- ----------------------------
DROP TABLE IF EXISTS `sms_activity`;
CREATE TABLE `sms_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '活动名称 (如: 2月会员日)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `type` tinyint(1) DEFAULT '1' COMMENT '类型: 1折扣活动 2积分兑换商品',
  `discount_rate` decimal(3,2) DEFAULT '1.00' COMMENT '折扣率 (如0.85)',
  `point_cost` int(11) DEFAULT '0' COMMENT '兑换所需积分',
  `product_id` bigint(20) DEFAULT NULL COMMENT '关联商品ID (若是兑换活动)',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 1启用 0停用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销活动与积分商城表';

-- ----------------------------
-- 11. 商品评价表 (pms_comment)
-- [数据联动]: 评价数据用于协同过滤算法分析
-- ----------------------------
DROP TABLE IF EXISTS `pms_comment`;
CREATE TABLE `pms_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `star` int(1) DEFAULT '5' COMMENT '星级 1-5',
  `content` text COMMENT '评价内容',
  `images` text COMMENT '评价图片(JSON数组)',
  `audit_status` tinyint(1) DEFAULT '0' COMMENT '审核状态: 0待审核 1通过 2拒绝',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评价表';

SET FOREIGN_KEY_CHECKS = 1;
