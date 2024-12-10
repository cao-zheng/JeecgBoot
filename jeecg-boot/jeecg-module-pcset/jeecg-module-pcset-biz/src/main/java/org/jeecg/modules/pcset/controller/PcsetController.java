package org.jeecg.modules.pcset.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.entity.FtpFileMain;
import org.jeecg.modules.pcset.service.FtpFileMainServiceImpl;
import org.jeecg.modules.pcset.service.PcsetServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

@Api(tags = "pcset示例")
@RestController
@RequestMapping("/pcset")
@Slf4j
public class PcsetController {

	@Autowired
	private PcsetServiceImpl pcsetService;

	@ApiOperation(value = "checkUpdate",notes = "校验版本号")
	@PostMapping("/checkUpdate")
	public CheckUpdateDto checkUpdate(@RequestBody CheckUpdateRequestDto checkUpdateDto) throws Exception{
		//ftp获取最新版
		return pcsetService.checkUpdate(checkUpdateDto);
	}
	@GetMapping("/download/{md5}")
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String md5) throws IOException {
		return pcsetService.downloadFile(md5);
	}
}
