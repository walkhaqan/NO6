-- 通知记录表
DROP TABLE IF EXISTS `tongzhijilu`;
CREATE TABLE `tongzhijilu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `yuyuebianhao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预约编号',
  `tongzhileixing` int(11) DEFAULT NULL COMMENT '通知类型（1-预约成功通知 2-就诊前24小时提醒 3-就诊前1小时提醒 4-就诊前15分钟提醒）',
  `tongzhineirong` longtext COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `zhanghao` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收账号',
  `shouji` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收手机',
  `fasongzhuangtai` int(11) DEFAULT '0' COMMENT '发送状态（0-待发送 1-发送中 2-发送成功 3-发送失败）',
  `chongshicishu` int(11) DEFAULT '0' COMMENT '重试次数',
  `jihuafasongshijian` datetime DEFAULT NULL COMMENT '计划发送时间',
  `shijifasongshijian` datetime DEFAULT NULL COMMENT '实际发送时间',
  `shibaiyuanyin` longtext COLLATE utf8mb4_unicode_ci COMMENT '失败原因',
  `duquzhuangtai` int(11) DEFAULT '0' COMMENT '用户读取状态（0-未读 1-已读）',
  `duqushijian` datetime DEFAULT NULL COMMENT '用户读取时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知记录';
