package org.jeecg.modules.pcset.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.pcset.entity.PcsetHelloEntity;

/**
 * 测试接口
 */
public interface IPcsetHelloService extends IService<PcsetHelloEntity> {

    String hello();

}
