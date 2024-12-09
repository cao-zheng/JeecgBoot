package org.jeecg.modules.pcset.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.service.PcsetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "pcset示例")
@RestController
@RequestMapping("/pcset")
@Slf4j
public class PcsetController {

	@Autowired
	private PcsetServiceImpl pcsetService;

	@ApiOperation(value = "checkUpdate",notes = "校验版本号")
	@PostMapping("/checkUpdate")
	public void checkUpdate(@RequestBody CheckUpdateRequestDto checkUpdateDto) throws Exception{
		//ftp获取最新版
		pcsetService.checkUpdate(checkUpdateDto);
	}
}
