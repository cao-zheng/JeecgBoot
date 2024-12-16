package org.jeecg.modules.pcset.dto;

import lombok.Data;

@Data
public class DownloadDto {
    private String md5;
    private String path;
    private String username;
    private String ip;
}
