package org.jeecg.modules.pcset.api;
import org.jeecg.modules.pcset.api.fallback.PcsetHelloFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "jeecg-pcset", fallbackFactory = PcsetHelloFallback.class)
public interface PcsetHelloApi {

    /**
     * pcset hello 微服务接口
     * @param
     * @return
     */
    @GetMapping(value = "/pcset/hello")
    String callHello();
}
