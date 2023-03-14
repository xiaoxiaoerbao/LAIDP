package main.utils;

import java.io.*;
import java.util.ArrayList;
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
            br=IOUtils.getTextGzipReader(file.getAbsolutePath());
        }else {
            br=IOUtils.getTextReader(file.getAbsolutePath());
        }
        return br;
    }

    public static BufferedReader getReader(String file){
        return getReader(new File(file));
    }

    public static BufferedReader getReader(Process p){
       return new BufferedReader(new InputStreamReader(p.getInputStream()));
    }

    public static BufferedWriter getWriter(File file){
       if (file.getName().endsWith("gz")) return IOUtils.getTextGzipWriter(file.getAbsolutePath());
       return IOUtils.getTextWriter(file.getAbsolutePath());
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

    public static List<String> readAllLines(String file){
        List<String> lines=new ArrayList<>();
        String line;
        try (BufferedReader br = IOTool.getReader(file)) {
            while ((line=br.readLine())!=null){
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert lines!=null : " check "+file;
        return lines;
    }

    public static void writeAllLines(File outFile, List<String> lines){
        try (BufferedWriter bw = IOTool.getWriter(outFile)) {
            for (String line:lines){
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<File> getLogFile(String title, String logDir, int logFileNum){
        assert logFileNum > 0 : logFileNum + " must be greater than 0";
        int digitsNum=(int)(Math.log10(logFileNum) +1);
        List<File> logFiles= new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        File file;
        for (int i = 0; i < logFileNum; i++) {
            sb.setLength(0);
            sb.append(title).append("_").append(PStringUtils.getNDigitNumber(digitsNum, i)).append(".log");
            file = new File(logDir, sb.toString());
            logFiles.add(file);
        }
        return logFiles;
    }
}
