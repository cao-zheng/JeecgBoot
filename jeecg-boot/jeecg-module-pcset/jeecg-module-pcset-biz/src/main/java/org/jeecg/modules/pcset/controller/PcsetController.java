package org.jeecg.modules.pcset.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.compress.utils.Lists;
import org.jeecg.modules.pcset.dto.DownloadDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.entity.FtpFileMain;
import org.jeecg.modules.pcset.service.FtpFileMainServiceImpl;
import org.jeecg.modules.pcset.service.PcsetServiceImpl;
import org.jeecg.modules.pcset.vo.FileListVo;
import org.jeecg.modules.pcset.vo.FileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
	public ResponseEntity<InputStreamResource> downloadFile(@RequestBody DownloadDto downloadDto) throws IOException {
		return pcsetService.downloadFile(downloadDto.getMd5());
	}

	@PostMapping("/file/list")
	public FileListVo getCurFileList() throws Exception{
		FileListVo fileListVo = new FileListVo();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String matchUri = request.getRequestURI();
		String filePath = "D://FtpFileHome/PCSet_Release";
		File file = new File(filePath);
		File[] files = file.listFiles();
		List<FileVo> fileVoList = Lists.newArrayList();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		for (File fileItem : files) {
			FileVo fileVo = new FileVo();
			String filename = "";
			if(fileItem.isDirectory()){
				filename = fileItem.getName() + "/";
			}else{
				filename = fileItem.getName();
			}
			fileVo.setName(filename);
			fileVo.setSize(fileItem.length() + " bytes");
			if(!matchUri.endsWith("/")){
				matchUri = matchUri + "/";
			}
			fileVo.setRelatePath(matchUri + fileItem.getName());
			fileVo.setUpdateTime(sdf.format(fileItem.lastModified()));
			fileVoList.add(fileVo);
		}

		if(!"/".equals(matchUri)){
			String[] dirArray = matchUri.split("/");
			String prevUri = "/";
			if(dirArray.length > 1){
				dirArray = Arrays.copyOf(dirArray,dirArray.length - 1);
				if(dirArray.length > 1){
					prevUri = String.join("/",dirArray);
				}
			}
			fileListVo.setPrevDir(prevUri);
		}
		fileListVo.setFileVoList(fileVoList);
		return fileListVo;
	}
}
