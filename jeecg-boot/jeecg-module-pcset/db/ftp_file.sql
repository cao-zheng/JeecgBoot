CREATE TABLE `ftp_file` (
  `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上传人',
  `create_time` datetime DEFAULT NULL COMMENT '上传日期',
  `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属部门',
  `file_name` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件名称',
  `file_size` longtext COLLATE utf8mb4_unicode_ci COMMENT '附件大小',
  `file_path` longtext COLLATE utf8mb4_unicode_ci COMMENT '附件路径',
  `file_md5` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件md5',
  `file_status` int DEFAULT '1' COMMENT '附件状态',
  `file_notes` longtext COLLATE utf8mb4_unicode_ci COMMENT '附件描述',
  `file_store_type` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT 'ftp' COMMENT '附件存储方式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;