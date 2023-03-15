package utils;

import java.io.*;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author xudaxing
 */
public class IOTool extends IOUtils {

    public static BufferedReader getReader(File file){
        BufferedReader br;
        if (file.getName().endsWith("gz")){
            br= getTextGzipReader(file.getAbsolutePath());
        }else {
            br= getTextReader(file.getAbsolutePath());
        }
        return br;
    }

    public static BufferedReader getReader(String file){
        return getReader(new File(file));
    }

    public static BufferedWriter getWriter(File file){
       if (file.getName().endsWith("gz")) return getTextGzipWriter(file.getAbsolutePath());
       return getTextWriter(file.getAbsolutePath());
    }

    public static BufferedWriter getWriter(String file){
        return getWriter(new File(file));
    }

    /**
     * 递归获取当前目录下的所有非隐藏文件和非隐藏目录
     * @param dir dir
     * @return 当前目录下的所有非隐藏文件和非隐藏目录
     */
    public static List<File> getVisibleFileRecursiveDir(String dir){
        File inputDirFile = new File(dir);
        TreeSet<File> fileTree = new TreeSet<>();
        for (File entry : inputDirFile.listFiles()) {
            if (entry.isHidden()) continue;
            if (entry.isFile()){
                fileTree.add(entry);
            }
            else {
                fileTree.addAll(getVisibleFileRecursiveDir(entry.getAbsolutePath()));
            }
        }
        return fileTree.stream().sorted().collect(Collectors.toList());
    }

}
