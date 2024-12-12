package org.jeecg.modules.pcset.vo;

import lombok.Data;

import java.util.List;

@Data
public class TreeItem {
    private String title;
    private String key;
    private List<TreeItem> children;
}
