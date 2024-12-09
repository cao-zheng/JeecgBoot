package org.jeecg.modules.pcset.service;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateReponseDto;
import org.jeecg.modules.pcset.dto.checkupdate.CheckUpdateRequestDto;
import org.jeecg.modules.pcset.entity.PcsetEntity;
import org.jeecg.modules.pcset.mapper.PcsetMapper;
import org.jeecg.modules.pcset.util.FtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试Service
 */
@Service
public class PcsetServiceImpl extends ServiceImpl<PcsetMapper, PcsetEntity> {
    @Autowired
    private FtpUtil ftpUtil;

    public CheckUpdateReponseDto checkUpdate(CheckUpdateRequestDto checkUpdateDto) throws Exception{
        CheckUpdateReponseDto checkUpdateReponseDto = new CheckUpdateReponseDto();

        //传入当前客户端版本号
        String curVersion = checkUpdateDto.getVersion();

        //获取ftp版本号列表
        List<FTPFile> ftpFileList = ftpUtil.getFtpFolder("/PCSet_Release");
        List<String> ftpNames = ftpFileList.stream().map(FTPFile::getName).collect(Collectors.toList());

        //判断ftp中版本号最大值
        String ftpMaxValue = Collections.max(ftpNames,this::compareVersion);

        //比较最大值 0相等， 1取maxValue + "V" -1取version
        if(compareVersion(ftpMaxValue,curVersion)>=0){
            FTPFile ftpFile = ftpFileList.stream()
                    .filter(item->item.getName().equals(ftpMaxValue))
                    .collect(Collectors.toList()).get(0);
            //获取时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");// 设置你想要的格式
            String dateStr = df.format(ftpFile.getTimestamp().getTime());


            checkUpdateReponseDto.setStatus("success");
            checkUpdateReponseDto.setIsUpdate(true);
            checkUpdateReponseDto.setIsForcibly(false);
            checkUpdateReponseDto.setSoft_Version(ftpMaxValue);
            checkUpdateReponseDto.setSoft_Release_Time(dateStr);

        }else{
            //ftp无版本号
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
