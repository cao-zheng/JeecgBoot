package org.jeecg.modules.pcset.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jeecg.modules.pcset.config.FTPClientFactory;
import org.jeecg.modules.pcset.dto.FtpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class FTPPoolServiceImpl {

    /**
     * ftp 连接池生成
     */
    private GenericObjectPool<FTPClient> pool;

    /**
     * ftp 客户端配置文件
     */
    @Autowired
    private FtpConfig config;

    /**
     * ftp 客户端工厂
     */
    @Autowired
    private FTPClientFactory factory;

    /**
     * 初始化pool
     */
    @PostConstruct
    private void initPool() {
        this.pool = new GenericObjectPool<FTPClient>(this.factory, this.config);
    }

    /**
     * 获取ftpClient
     */
    public FTPClient borrowObject() {
        if (this.pool != null) {
            try {
                return this.pool.borrowObject();
            } catch (Exception e) {
                log.error("获取 FTPClient 失败 ", e);
            }
        }
        return null;
    }

    /**
     * 归还 ftpClient
     */
    public void returnObject(FTPClient ftpClient) {
        if (this.pool != null && ftpClient != null) {
            this.pool.returnObject(ftpClient);
        }
    }

    public FtpConfig getFtpPoolConfig() {
        return config;
    }
}