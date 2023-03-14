package main.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Fei Lu
 */
public class IOUtils {
    
    public static BufferedReader getTextGzipReader (String infileS) {
        BufferedReader br = null;
        try {
            //br = new BufferedReader(new InputStreamReader(new MultiMemberGZIPInputStream(new FileInputStream(infileS))));
            br = new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(Paths.get(infileS)), 65536)), 65536);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return br;
    }
    
    public static BufferedWriter getTextGzipWriter (String outfileS) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(Paths.get(outfileS)), 65536)), 65536);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bw;
    }
    
    public static BufferedWriter getTextWriter (String outfileS) {
        BufferedWriter bw = null;
        try {
             bw = new BufferedWriter (new FileWriter(outfileS), 65536);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bw;
    }

    public static BufferedReader getTextReader (String infileS) {
        BufferedReader br = null;
        try {
            br = new BufferedReader (new FileReader(infileS), 65536);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return br;
    }
    
    public static List<File> getFileListInDirStartsWith (String inDirS, String startStr) {
        File[] fs = new File(inDirS).listFiles();
        fs = listFilesStartsWith(fs, startStr);
        List<File> fList = new ArrayList<>(Arrays.asList(fs));
        Collections.sort(fList);
        return fList;
    }
    
    public static File[] listFilesStartsWith (File[] fAll, String startStr) {
        List<File> al = new ArrayList<>();
        for (File file : fAll) {
            if (file.getName().startsWith(startStr)) al.add(file);
        }
        return al.toArray(new File[0]);
    }

}
