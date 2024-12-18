package org.jeecg.modules.pcset.service;


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.twmacinta.util.MD5;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPFile;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.pcset.config.ThreadPoolConfig;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.entity.FtpFileMain;
import org.jeecg.modules.pcset.entity.PcsetEntity;
import org.jeecg.modules.pcset.mapper.PcsetMapper;
import org.jeecg.modules.pcset.util.FileUtil;
import org.jeecg.modules.pcset.util.FtpUtil;
import org.jeecg.modules.pcset.vo.FileVo;
import org.jeecg.modules.pcset.vo.TreeItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 测试Service
 */
@Service
public class PcsetServiceImpl extends ServiceImpl<PcsetMapper, PcsetEntity> {

    @Value(value = "${apache.ftp.homedirectory}")
    private String ftp_homedirectory;

    @Autowired
    private FileUtil fileUtil;

    private static final String ftpPathChar = "/";

    @Autowired
    private FtpFileMainServiceImpl ftpFileMainService;

    public static final Map<String,FtpFileMain> ftpFileMain_Map = new ConcurrentHashMap<>();

    public ResponseEntity<InputStreamResource> downloadPathFile(String path) throws IOException {
        File file = new File(path);
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        ResponseEntity<InputStreamResource> resourceResponseEntity =  ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        ThreadPoolConfig.Download_Thread_Pool.execute(()->{
            //更新下载次数
            try {
                updateFileDownloadCount(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return resourceResponseEntity;
    }

    public void updateFileDownloadCount(File file) throws IOException {
        String filePath = file.getCanonicalPath().replace("\\", "/");
        LambdaQueryWrapper<FtpFileMain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FtpFileMain::getFilePath, filePath);
        FtpFileMain ftpFileMain = ftpFileMainService.getOne(queryWrapper, false);
        ftpFileMain.setDownloadCount(ftpFileMain.getDownloadCount() + 1);
        ftpFileMainService.saveOrUpdate(ftpFileMain);

        FtpFileMain ftpFileMainCache = ftpFileMain_Map.get(filePath);
        if(Objects.nonNull(ftpFileMainCache)){
            ftpFileMain_Map.put(filePath,ftpFileMain);
        }else{
            ftpFileMainCache.setDownloadCount(ftpFileMainCache.getDownloadCount() + 1);
            ftpFileMain_Map.put(filePath,ftpFileMainCache);
        }
    }

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

        String fileDir = ftp_homedirectory + ftpPathChar + "PCSet_Release";
        String updateDir = "UpdatePackage";

        CheckUpdateDto checkUpdateReponseDto = new CheckUpdateDto();

        //传入当前客户端版本号
        String curVersion = checkUpdateDto.getVersion();

        //获取ftp版本号列表
        List<String> ftpNames = Arrays.stream(
                    Objects.requireNonNull(new File(fileDir).listFiles())
                ).filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toList());

        //判断ftp中版本号最大值
        String ftpMaxValue = Collections.max(ftpNames,this::compareVersion);

        //比较最大值 0相等， 1取maxValue
        int resultVersionValue = compareVersion(ftpMaxValue,curVersion);
        if(resultVersionValue > 0) {
            //该条件下客户端需要更新

            String updateFileUrl = fileDir
                    + ftpPathChar
                    + ftpMaxValue
                    + ftpPathChar
                    + updateDir
                    + ftpPathChar;
            File updateFileMsg = Arrays.stream(Objects.requireNonNull(new File(updateFileUrl).listFiles()))
                    .filter(File::isFile)
                    .collect(Collectors.toList()).get(0);

            //数据库查询md5
            String md5 = "";
            String dateStr = "";

            LambdaQueryWrapper<FtpFileMain> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(FtpFileMain::getFilePath, updateFileUrl + updateFileMsg.getName());
            FtpFileMain ftpFileMain = ftpFileMainService.getOne(queryWrapper, false);
            String releaseNotes = "";
            if (Objects.nonNull(ftpFileMain)){
                md5 = ftpFileMain.getFileMd5();
                releaseNotes = ftpFileMain.getFileNotes();
                dateStr = DateUtil.format(ftpFileMain.getUpdateTime(), "yyyy-MM-dd hh:mm:ss");
            }else {
                File file = new File( updateFileUrl + updateFileMsg.getName());
                dateStr = DateUtil.format(new Date(file.lastModified()), "yyyy-MM-dd hh:mm:ss");
                md5 = MD5.asHex(MD5.getHash(file));
            }

            checkUpdateReponseDto.setStatus("success");
            checkUpdateReponseDto.setIsUpdate(true);
            checkUpdateReponseDto.setIsForcibly(false);
            checkUpdateReponseDto.setSoft_Version(ftpMaxValue);
            checkUpdateReponseDto.setSoft_Release_Time(dateStr);
            checkUpdateReponseDto.setSoft_Release_Notes(releaseNotes);
            checkUpdateReponseDto.setSoft_Download_Url("/pcset/download");//返回值api网关处理
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

    public TreeItem getCurFilePackageList() throws Exception {
        return fileUtil.buildPageTreeJson(ftp_homedirectory);
    }



    @PostConstruct
    public void initDownLoadCount(){
        List<FtpFileMain> ftpFileMains = ftpFileMainService.list();
        for (FtpFileMain ftpFileMain : ftpFileMains) {
            ftpFileMain_Map.put(ftpFileMain.getFilePath(),ftpFileMain);
        }
    }

    public List<FileVo> getCurFileAndPackageList(String path) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        LambdaQueryWrapper<FtpFileMain> queryWrapper = new LambdaQueryWrapper<>();
        //根据path获取文件和文件夹
        File fileModel = new File(path);
        File[] filesArys = fileModel.listFiles();
        List<FileVo> fileVoList = new ArrayList<>();
        for (File file : filesArys) {
            FileVo fileVo = new FileVo();
            if(file.isFile()){
                fileVo.setName(file.getName());
                fileVo.setSize(String.valueOf(file.length()));
                fileVo.setUpdateTime(sf.format(file.lastModified()));
                fileVo.setType("file");
                fileVo.setRelatePath(file.getCanonicalPath());

                String filePath = file.getCanonicalPath().replace("\\", "/");
                if(Objects.nonNull(ftpFileMain_Map.get(filePath))){
                    fileVo.setDownloadCount(String.valueOf(ftpFileMain_Map.get(filePath).getDownloadCount()));
                }else{
                    queryWrapper.eq(FtpFileMain::getFilePath,filePath);
                    FtpFileMain ftpFileMain = ftpFileMainService.getOne(queryWrapper, false);
                    fileVo.setDownloadCount(String.valueOf(ftpFileMain.getDownloadCount()));
                }
            }else{
                fileVo.setName(file.getName() + "/");
                fileVo.setUpdateTime(sf.format(file.lastModified()));
                fileVo.setSize("-");
                fileVo.setType("directory");
                fileVo.setRelatePath(file.getPath());
            }
            fileVoList.add(fileVo);
        }
        return fileVoList;
    }

    public List<FileVo> getCurFileList(String path,String isAll) throws IOException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if("true".equals(isAll)){
            path = ftp_homedirectory;
        }
        File file = new File(path);
        List<File> allFileList = new ArrayList<>();
        //获取文件夹目录下所有文件
        fileUtil.getPackageFileList(file,allFileList);

        List<FileVo> fileVoList = new ArrayList<>();
        for (File fileItem : allFileList) {
            FileVo fileVo = new FileVo();
            fileVo.setName(fileItem.getName());
            fileVo.setUpdateTime(sf.format(new Date(fileItem.lastModified())));
            fileVo.setSize(String.valueOf(fileItem.length()));
            fileVo.setRelatePath(fileItem.getCanonicalPath());
            fileVoList.add(fileVo);
        }
        return fileVoList;
    }

    /**
     * X.X.X比较
     * @param v1
     * @param v2
     * @return
     */
    private int compareVersion(String v1, String v2) {
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
