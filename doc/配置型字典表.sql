CREATE TABLE `sys_dictionary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dict_type` varchar(50) NOT NULL COMMENT '字典类型（如：order_status）',
  `dict_code` varchar(50) NOT NULL COMMENT '字典编码（如：PENDING）',
  `dict_label` varchar(100) NOT NULL COMMENT '字典标签（如：待审核）',
  `dict_value` varchar(100) COMMENT '字典值（如：10）',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `css_tag` varchar(50) COMMENT '样式标签',
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认值',
  `is_editable` tinyint DEFAULT 1 COMMENT '是否可编辑',
  `is_deleted` tinyint DEFAULT 0,
  `create_time` datetime,
  `update_time` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dict_type_code` (`dict_type`, `dict_code`),
  KEY `idx_dict_type` (`dict_type`, `sort_order`)
);

CREATE TABLE `sys_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
	`dict_type` varchar(50) COMMENT 'sys_dictionary 表dict_type。引用的字典类型（如：order_status）',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(200) COMMENT '描述',
  `category` varchar(50) COMMENT '分组',
  `value_type` varchar(20) DEFAULT 'string' COMMENT '值类型：string, int, bool, json, enum',
  `enum_values` text COMMENT '枚举可选值',
  `enum_type` varchar(50) COMMENT '代码枚举类名',
  `is_editable` tinyint DEFAULT 1,
  `is_deleted` tinyint DEFAULT 0,
  `update_time` datetime,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
);