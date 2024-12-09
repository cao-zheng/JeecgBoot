package org.jeecg.modules.pcset.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.pcset.entity.PcsetHelloEntity;
import org.jeecg.modules.pcset.service.IPcsetHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "pcset示例")
@RestController
@RequestMapping("/pcset")
@Slf4j
public class PcsetHelloController {

	@Autowired
	private IPcsetHelloService jeecgHelloService;

	@ApiOperation(value = "hello", notes = "对外服务接口")
	@GetMapping(value = "/hello")
	public String sayHello() {
		log.info(" ---我被调用了--- ");
		return jeecgHelloService.hello();
	}

}
