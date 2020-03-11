CREATE TABLE `grade_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `grade` int(10) unsigned NOT NULL COMMENT '对应积分等级',
  `integral` int(10) unsigned NOT NULL COMMENT '最高历史积分',
  PRIMARY KEY (`id`),
  KEY `grade` (`grade`) USING BTREE,
  KEY `integral` (`integral`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户等级配置表';

CREATE TABLE `integral_record` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL COMMENT '用户id',
  `integral` int(11) NOT NULL DEFAULT '0' COMMENT '本次操作的积分',
  `history` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '剩余积分',
  `order_id` int(10) unsigned NOT NULL COMMENT '订单id',
  `order_type` tinyint(4) unsigned NOT NULL COMMENT '订单类型',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户积分记录表';

CREATE TABLE `pay_order` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL COMMENT '用户id',
  `product_id` int(10) unsigned NOT NULL COMMENT '商品id',
  `wallet_id` int(10) unsigned COMMENT '用户钱包id',
  `order_number` varchar(255) NOT NULL COMMENT '订单号',
  `integral` int(11) NOT NULL DEFAULT '0' COMMENT '商品的积分',
  `product_count` int(10) unsigned NOT NULL COMMENT '商品数量',
  `total_price` double unsigned NOT NULL COMMENT '商品总额',
  `status` tinyint(2) unsigned NOT NULL COMMENT '订单状态',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_number` (`order_number`),
  KEY `version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品支付订单';

CREATE TABLE `products` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `price` double unsigned NOT NULL DEFAULT '0' COMMENT '商品价格',
  `stock` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '设定库存',
  `surplus_stock` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '剩余库存',
  `commit_stock` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '已提交库存',
  `integral` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '商品积分',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  KEY `version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';

CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(255) NOT NULL COMMENT '用户昵称',
  `integral` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '剩余积分',
  `grade` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '等级',
  `order_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '订单数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息表';

CREATE TABLE `wallet` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL COMMENT '用户id',
  `card_name` varchar(255) NOT NULL COMMENT '银行卡名称',
  `balance` double unsigned NOT NULL COMMENT '银行卡余额',
  `commit_balance` double unsigned NOT NULL DEFAULT '0' COMMENT '锁定金额',
  `no` tinyint(2) NOT NULL DEFAULT '0' COMMENT '支付权重',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户钱包信息';