package org.jeecg.modules.pcset;

import cn.hutool.json.JSONUtil;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.modules.pcset.vo.TreeItem;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Test1 {
    public static void main(String[] args) {
        List<TreeItem> treeItemRoot = new ArrayList<>();
        String filePath = "D://FtpFileHome";
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File file1 : files) {
            System.out.println(file1.getName());
        }
        List<String> strs = listAllFiles("D://FtpFileHome");
        System.out.println(JSONUtil.toJsonStr(strs));
    }

    public static List<String> listAllFiles(String folderPath) {
        List<String> fileList = new ArrayList<>();

        // 创建一个File对象，表示要查找的文件夹
        File folder = new File(folderPath);

        // 检查文件夹是否存在
        if (folder.exists() && folder.isDirectory()) {
            // 获取文件夹中的所有文件和子文件夹
            File[] files = folder.listFiles();

            if (files != null) {
                for (File file : files) {
                    // 判断当前项是否为文件
                    if (file.isFile()) {
                        // 添加文件名到列表中
                        fileList.add(file.getName());
                    } else if (file.isDirectory()) {
                        // 如果是文件夹，递归调用listAllFiles方法来获取子文件夹中的文件名
                        List<String> subFolderFiles = listAllFiles(file.getAbsolutePath());
                        fileList.addAll(subFolderFiles);
                    }
                }
            }
        } else {
            fileList.add("文件夹不存在或不是一个文件夹");
        }
        return fileList;
    }
}
