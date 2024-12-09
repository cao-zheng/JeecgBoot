package org.jeecg.modules.pcset.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.pcset.entity.PcsetHelloEntity;
import org.jeecg.modules.pcset.mapper.PcsetHelloMapper;
import org.jeecg.modules.pcset.service.IPcsetHelloService;
import org.springframework.stereotype.Service;

/**
 * 测试Service
 */
@Service
public class PcsetHelloServiceImpl extends ServiceImpl<PcsetHelloMapper, PcsetHelloEntity> implements IPcsetHelloService {

    @Override
    public String hello() {
        return "hello ，我是 pcset 微服务节点!";
    }
}
