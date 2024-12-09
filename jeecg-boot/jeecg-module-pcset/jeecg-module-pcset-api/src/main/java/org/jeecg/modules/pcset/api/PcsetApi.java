package org.jeecg.modules.pcset.api;
import org.jeecg.modules.pcset.api.fallback.PcsetFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "jeecg-pcset", fallbackFactory = PcsetFallback.class)
public interface PcsetApi {

    /**
     * pcset hello 微服务接口
     * @param
     * @return
     */
    @GetMapping(value = "/pcset/hello")
    String callHello();
}
