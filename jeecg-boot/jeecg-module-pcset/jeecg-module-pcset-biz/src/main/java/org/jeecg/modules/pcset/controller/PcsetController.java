package org.jeecg.modules.pcset.controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.pcset.dto.DownloadDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.service.PcsetServiceImpl;
import org.jeecg.modules.pcset.vo.TreeItem;
import org.jeecg.modules.pcset.vo.FileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@ApiOperation(value = "download",notes = "附件下载")
	@PostMapping("/download")
	@AutoLog(value="download")
	public ResponseEntity<InputStreamResource> downloadFile(@RequestBody DownloadDto downloadDto) throws IOException {
		return pcsetService.downloadFile(downloadDto.getMd5());
	}

	@PostMapping("/file/list")
	public List<TreeItem> getCurAllFilePackageList() throws Exception{
		List<TreeItem> treeItems = new ArrayList<>();
		treeItems.add(pcsetService.getCurFilePackageList());
		return treeItems;
	}
}
