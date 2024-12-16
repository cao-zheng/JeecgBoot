package org.jeecg.modules.pcset.controller;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.pcset.dto.DownloadDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.service.PcsetServiceImpl;
import org.jeecg.modules.pcset.vo.FileVo;
import org.jeecg.modules.pcset.vo.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

	@ApiOperation(value = "downloadFile",notes = "附件下载")
	@PostMapping("/download")
	@AutoLog(value="download")
	public ResponseEntity<InputStreamResource> downloadFile(@RequestBody DownloadDto downloadDto) throws IOException {
		if(StringUtils.isNotEmpty(downloadDto.getMd5())) return pcsetService.downloadFile(downloadDto.getMd5());
		else return pcsetService.downloadPathFile(downloadDto.getPath());
	}

	@ApiOperation(value = "getCurAllFilePackageList",notes = "文件夹列表")
	@PostMapping("/file/package/list")
	public List<TreeItem> getCurAllFilePackageList() throws Exception{
		return pcsetService.getCurFilePackageList().getChildren();
	}

	@PostMapping("/file/list")
	public List<FileVo> getPackageFileList(@RequestBody Map<String,String> map) throws Exception{
		String isAll = Objects.nonNull(map.get("isAll"))?map.get("isAll").toString():"";
		String filepath = Objects.nonNull(map.get("path"))?map.get("path").toString():"";
		return pcsetService.getCurFileList(filepath,isAll);
	}
}
