package org.jeecg.modules.pcset.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
@Component
@Slf4j
public class FtpUtil {
    @Autowired
    FTPClient ftpClient;
    /**
     * 获取指定目录下所有文件夹
     * @param folderPath 指定目录
     * @return           文件夹列表
     * @throws Exception
     */
    public List<FTPFile> getFtpFolder(String folderPath) throws Exception{
        List<FTPFile> ftpFiles = Lists.newArrayList();
        ftpClient.changeWorkingDirectory(new String(folderPath.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
        FTPFile[] files = ftpClient.listFiles();
        for (FTPFile file : files) {
            if(file.isDirectory())ftpFiles.add(file);
        }
        return ftpFiles;
    }
}
