package org.jeecg.modules.pcset.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.pcset.dto.DownloadDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.service.PcsetServiceImpl;
import org.jeecg.modules.pcset.vo.FileVo;
import org.jeecg.modules.pcset.vo.TreeItem;
import org.jeecgframework.poi.exception.excel.ExcelExportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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

	@ApiOperation(value = "getRootPath",notes = "获取附件根路径")
	@PostMapping("/getRootPath")
	public String getRootPath() throws Exception{
		return pcsetService.getRootPath() + "/";
	}

	@ApiOperation(value = "checkUpdate",notes = "pcset校验版本号")
	@PostMapping("/checkUpdate")
	public CheckUpdateDto checkUpdate(@RequestBody CheckUpdateRequestDto checkUpdateDto) throws Exception{
		return pcsetService.checkUpdate(checkUpdateDto);
	}

	@ApiOperation(value = "downloadFile",notes = "附件下载（md5和路径方式）")
	@PostMapping("/download")
	@CrossOrigin
	public ResponseEntity<InputStreamResource> downloadFile(@RequestBody DownloadDto downloadDto) throws IOException {
		if(StringUtils.isNotEmpty(downloadDto.getMd5())) return pcsetService.downloadFile(downloadDto.getMd5());
		else return pcsetService.downloadPathFile(downloadDto.getPath());
	}


	@ApiOperation(value = "getCurAllFilePackageList",notes = "所有文件夹列表,包含子文件夹显示")
	@PostMapping("/file/package/list")
	public List<TreeItem> getCurAllFilePackageList() throws Exception{
		return pcsetService.getCurFilePackageList().getChildren();
	}

	@ApiOperation(value = "getPackageFileList",notes = "根据文件夹路径获取文件夹下文件（递归）")
	@PostMapping("/file/list")
	public List<FileVo> getPackageFileList(@RequestBody Map<String,String> map) throws Exception{
		String isAll = Objects.nonNull(map.get("isAll"))?map.get("isAll").toString():"";
		String filepath = Objects.nonNull(map.get("path"))?map.get("path").toString():"";
		return pcsetService.getCurFileList(filepath,isAll);
	}

	@ApiOperation(value = "getCurFileAndPackageList",notes = "获取当前目录下文件和文件夹")
	@PostMapping("/file/package/cur/list")
	@CrossOrigin
	public List<FileVo> getCurFileAndPackageList(@RequestBody Map<String,String> map) throws Exception{
		String filepath = Objects.nonNull(map.get("path"))?map.get("path").toString():"";
		return pcsetService.getCurFileAndPackageList(filepath);
	}
}
