package org.jeecg.modules.pcset.util;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.pcset.vo.TreeItem;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        for (File file : folder.listFiles()) {
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
