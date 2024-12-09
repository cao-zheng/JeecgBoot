package org.jeecg.modules.pcset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pcset.entity.FtpFile;
import org.jeecg.modules.pcset.mapper.FtpFileMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: ftp附件索引表
 * @Author: jeecg-boot
 * @Date:   2024-12-09
 * @Version: V1.0
 */
@Service
public class FtpFileServiceImpl extends ServiceImpl<FtpFileMapper, FtpFile> implements IService<FtpFile> {

}
