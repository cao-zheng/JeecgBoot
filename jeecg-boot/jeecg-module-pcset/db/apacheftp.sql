CREATE TABLE ftp_user (
                          userid VARCHAR(64) NOT NULL COMMENT "登录账号" UNIQUE,
                          userpassword VARCHAR(64) NOT NULL COMMENT "登录密码",
                          homedirectory VARCHAR(128) NOT NULL COMMENT "主目录，用户授权主目录",
                          enableflag BOOLEAN DEFAULT TRUE COMMENT "当前用户可用",
                          writepermission BOOLEAN DEFAULT FALSE COMMENT "具有上传权限",
                          idletime INT DEFAULT 0 COMMENT "空闲时间（为300秒）",
                          uploadrate INT DEFAULT 0 COMMENT "上传速率限制为480000字节每秒 0为不限制",
                          downloadrate INT DEFAULT 0 COMMENT "下载速率限制为480000字节每秒 0为不限制",
                          maxloginnumber INT DEFAULT 0 COMMENT "最大登陆用户数",
                          maxloginperip INT DEFAULT 0 COMMENT "同IP登陆用户数",
                          create_by VARCHAR(50) COMMENT "创建人",
                          create_time Datetime COMMENT "创建时间",
                          update_by VARCHAR(50) COMMENT "更新人",
                          update_time Datetime COMMENT "更新时间",
                          id VARCHAR(64) NOT NULL PRIMARY KEY COMMENT "主目录，用户授权主目录"
);