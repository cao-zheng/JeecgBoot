package org.jeecg.modules.pcset.service;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twmacinta.util.MD5;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.entity.FtpFileMain;
import org.jeecg.modules.pcset.entity.PcsetEntity;
import org.jeecg.modules.pcset.mapper.PcsetMapper;
import org.jeecg.modules.pcset.util.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 测试Service
 */
@Service
public class PcsetServiceImpl extends ServiceImpl<PcsetMapper, PcsetEntity> {

    @Value(value = "${apache.ftp.homedirectory}")
    private String ftp_homedirectory;

    private static final String ftpPathChar = "/";

    @Autowired
    private FtpUtil ftpUtil;

    @Autowired
    private FtpFileMainServiceImpl ftpFileMainService;


    public ResponseEntity<InputStreamResource> downloadFile(String md5) throws IOException {

        LambdaQueryWrapper<FtpFileMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FtpFileMain::getFileMd5,md5);
        FtpFileMain ftpFileMain = ftpFileMainService.getOne(queryWrapper,false);

        File file = new File(ftpFileMain.getFilePath());
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
    public CheckUpdateDto checkUpdate(CheckUpdateRequestDto checkUpdateDto) throws Exception{

        String fileDir = "PCSet_Release";
        String updateDir = "UpdatePackage";
        String installDir = "InstallPackage";

        CheckUpdateDto checkUpdateReponseDto = new CheckUpdateDto();

        //传入当前客户端版本号
        String curVersion = checkUpdateDto.getVersion();

        //获取ftp版本号列表
        List<FTPFile> ftpFileList = ftpUtil.getFtpCurFolderAndFile(fileDir);
        List<String> ftpNames = ftpFileList.stream()
                .filter(FTPFile::isDirectory)
                .map(FTPFile::getName)
                .collect(Collectors.toList());

        //判断ftp中版本号最大值
        String ftpMaxValue = Collections.max(ftpNames,this::compareVersion);

        //比较最大值 0相等， 1取maxValue
        int resultVersionValue = compareVersion(ftpMaxValue,curVersion);
        if(resultVersionValue > 0){
            //该条件下客户端需要更新

            String updateFileUrl = ftpPathChar
                    + fileDir
                    + ftpPathChar
                    + ftpMaxValue
                    + ftpPathChar
                    + updateDir
                    + ftpPathChar;
            //获取更新文件信息
            FTPFile updateFileMsg = ftpUtil.getFtpCurFolderAndFile(updateFileUrl)
                    .stream()
                    .filter(FTPFile::isFile)
                    .collect(Collectors.toList()).get(0);


            //数据库查询md5
            String md5 = "";
            LambdaQueryWrapper<FtpFileMain> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FtpFileMain::getFilePath,ftp_homedirectory + updateFileUrl +  updateFileMsg.getName());
            FtpFileMain ftpFileMain = ftpFileMainService.getOne(queryWrapper,false);
            if(Objects.nonNull(ftpFileMain))
                md5 = ftpFileMain.getFileMd5();
            else
                md5 = MD5.asHex(MD5.getHash(new File(ftp_homedirectory + updateFileUrl + updateFileMsg.getName())));

            //获取时间
            String dateStr = DateUtil.format(updateFileMsg.getTimestamp().getTime(), "yyyy-MM-dd hh:mm:ss");

            checkUpdateReponseDto.setStatus("success");
            checkUpdateReponseDto.setIsUpdate(true);
            checkUpdateReponseDto.setIsForcibly(false);
            checkUpdateReponseDto.setSoft_Version(ftpMaxValue);
            checkUpdateReponseDto.setSoft_Release_Time(dateStr);
            checkUpdateReponseDto.setSoft_Release_Notes("");
            checkUpdateReponseDto.setSoft_Download_Url("");
            checkUpdateReponseDto.setMsg("版本号落后，请更新");
            checkUpdateReponseDto.setSoft_Download_MD5(md5);
            checkUpdateReponseDto.setFilename(updateFileMsg.getName());

        }else if(resultVersionValue == 0){
            //ftp无版本号
            checkUpdateReponseDto.setStatus("success");
            checkUpdateReponseDto.setIsUpdate(false);
            checkUpdateReponseDto.setIsForcibly(false);
            checkUpdateReponseDto.setMsg("版本号一致，无需更新");

        }else{
            //ftp无版本号
            checkUpdateReponseDto.setStatus("fail");
            checkUpdateReponseDto.setIsUpdate(false);
            checkUpdateReponseDto.setIsForcibly(false);
            checkUpdateReponseDto.setMsg("无此版本号");
        }


        return checkUpdateReponseDto;
    }


    /**
     * X.X.X比较
     * @param v1
     * @param v2
     * @return
     */
    public int compareVersion(String v1, String v2) {
        String[] v1Parts = v1.split("\\.");
        String[] v2Parts = v2.split("\\.");

        for (int i = 0; i < Math.max(v1Parts.length, v2Parts.length); i++) {
            int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
            int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

            if (v1Part < v2Part) return -1;
            else if (v1Part > v2Part)  return 1;
        }
        return 0;
    }
}
