package org.jeecg.modules.pcset.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeItem {
    private String title;
    private String key;
    private long size;
    private String type;
    private List<TreeItem> children = new ArrayList<>();
}
