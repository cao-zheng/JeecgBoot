package org.jeecg.modules.pcset.api.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.jeecg.modules.pcset.api.PcsetApi;
import lombok.Setter;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author JeecgBoot
 */
@Slf4j
@Component
public class PcsetFallback implements FallbackFactory<PcsetApi> {
    @Setter
    private Throwable cause;

    @Override
    public PcsetApi create(Throwable throwable) {
        log.error("微服务接口调用失败： {}", cause);
        return null;
    }

}