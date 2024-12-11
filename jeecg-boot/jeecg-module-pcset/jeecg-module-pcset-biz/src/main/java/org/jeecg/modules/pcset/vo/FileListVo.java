package org.jeecg.modules.pcset.vo;

import lombok.Data;

import java.util.List;

@Data
public class FileListVo {
    private String prevDir;
    private List<FileVo> fileVoList;
}
