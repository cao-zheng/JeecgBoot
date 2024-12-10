package org.jeecg.modules.pcset.dto.checkupdate;

import lombok.Data;

@Data
public class CheckUpdateReponseDto {
    //状态码
    private String Status;
    //是否更新
    private boolean IsUpdate;
    //信息
    private String msg;
    //附件名称
    private String filename;
    //是否强制更新
    private boolean IsForcibly;
    //版本号
    private String Soft_Version;
    //发布时间
    private String Soft_Release_Time;
    //发布信息
    private String Soft_Release_Notes;
    //下载链接
    private String Soft_Download_Url;
    //该附件md5码
    private String Soft_Download_MD5;
}
