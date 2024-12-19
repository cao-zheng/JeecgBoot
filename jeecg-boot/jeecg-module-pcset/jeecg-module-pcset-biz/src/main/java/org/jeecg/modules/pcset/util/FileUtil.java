package org.jeecg.modules.pcset.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pcset.vo.TreeItem;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FileUtil {
    /**
     * 根据文件路径构建文件夹和文件树目录
     * @param path
     * @return
     * @throws Exception
     */
    public TreeItem buildAllTreeJson(String path) throws Exception {
        File root = new File(path);
        TreeItem rootNode = createNode(root);
        buildAllTree(rootNode, root);
        return rootNode;
    }

    /**
     * 根据文件路径构建文件夹树目录
     * @param path
     * @return
     * @throws Exception
     */
    public TreeItem buildPageTreeJson(String path) throws Exception {
        File root = new File(path);
        TreeItem rootNode = createNode(root);
        buildTree(rootNode, root);
        return rootNode;
    }
    private void buildTree(TreeItem node, File folder) {
        List<TreeItem> children = new ArrayList<>();
        //针对xxx.xxx.xxx版本号文件夹做排序
        List<File> fileFolders = Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                .filter(File::isDirectory)
                .sorted((o1, o2) -> compareVersion(o1.getName(), o2.getName()))
                .collect(Collectors.toList());

        for (File file : fileFolders) {
            if (file.isDirectory()) {
                TreeItem childNode = createNode(file);
                children.add(childNode);
                buildTree(childNode, file);
            }
        }
        if (!children.isEmpty()) {
            node.setChildren(children);
        }
    }

    private int compareVersion(String v1, String v2) {
        try{
            String[] v1Parts = v1.split("\\.");
            String[] v2Parts = v2.split("\\.");

            for (int i = 0; i < Math.max(v1Parts.length, v2Parts.length); i++) {
                int v1Part = i < v1Parts.length ? Integer.parseInt(v1Parts[i]) : 0;
                int v2Part = i < v2Parts.length ? Integer.parseInt(v2Parts[i]) : 0;

                if (v1Part < v2Part) return -1;
                else if (v1Part > v2Part)  return 1;
            }
        } catch(Exception ignored){}
        return 0;
    }


    private void buildAllTree(TreeItem node, File folder) {
        List<TreeItem> children = new ArrayList<>();
        for (File file : folder.listFiles()) {
            TreeItem childNode = createNode(file);
            children.add(childNode);
            if (file.isDirectory()) {
                buildAllTree(childNode, file);
            }
        }
        if (!children.isEmpty()) {
            node.setChildren(children);
        }
    }

    private static TreeItem createNode(File file) {
        TreeItem rootNode = new TreeItem();
        rootNode.setTitle(file.getName());
        rootNode.setKey(file.getAbsolutePath());
        rootNode.setType(file.isDirectory() ? "directory" : "file");
        rootNode.setSize(file.isFile() ? file.length() : 0);
        return rootNode;
    }

    public static void getPackageFileList(File file, List<File> allFileList){
        File[] fileList = file.listFiles();
        for (File fileItem : fileList) {
            if(fileItem.isDirectory()){
                getPackageFileList(fileItem,allFileList);
            }else{
                allFileList.add(fileItem);
            }
        }
    }
}
