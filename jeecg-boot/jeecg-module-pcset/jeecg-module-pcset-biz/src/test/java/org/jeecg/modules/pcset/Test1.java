package org.jeecg.modules.pcset;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.modules.pcset.vo.TreeItem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Test1 {
//    public static void main(String[] args) throws IOException {
//        TreeItem treeItemRoot = new TreeItem();
//        String filePath = "D://FtpFileHome";
//        File file = new File(filePath);
//        Files.walk(Paths.get(filePath)).forEach(path -> {
//            addNode(treeItemRoot,path);
//        });
//        System.out.println(JSONUtil.toJsonStr(treeItemRoot));
//    }

    public static void main(String[] args) throws Exception {
        String path = "D://FtpFileHome"; // 替换为你的目录路径
        String json = buildTreeJson(path);
        System.out.println(json);
    }

    public static String buildTreeJson(String path) throws Exception {
        File root = new File(path);
        Map<String, Object> rootNode = createNode(root);
        buildTree(rootNode, root);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(rootNode);
    }

    private static void buildTree(Map<String, Object> node, File folder) {
        List<Map<String, Object>> children = new ArrayList<>();
        for (File file : folder.listFiles()) {
            Map<String, Object> childNode = createNode(file);
            children.add(childNode);
            if (file.isDirectory()) {
                buildTree(childNode, file);
            }
        }
        if (!children.isEmpty()) {
            node.put("children", children);
        }
    }

    private static Map<String, Object> createNode(File file) {
        Map<String, Object> node = new HashMap<>();
        node.put("name", file.getName());
        node.put("type", file.isDirectory() ? "directory" : "file");
        node.put("size", file.isFile() ? file.length() : 0);
        return node;
    }
}
