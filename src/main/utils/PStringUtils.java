/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main.utils;


import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fei Lu 
 */
public class PStringUtils {
    
    /**
     * Return a string of number filled with 0 on the left
     * @param n
     * @param num
     * @return 
     */
    public static String getNDigitNumber (int n, int num) {
        StringBuilder s = new StringBuilder(String.valueOf(num));
        int cnt = n-s.length();
        for (int i = 0; i < cnt; i++) {
            s.insert(0, "0");
        }
        return s.toString();
    }


    
    /**
     * Return a list of split String using Guava splitter, delimiter tab
     * @param line
     * @return 
     */
    public static List<String> fastSplit (String line) {
        List<String> ls = fastSplit(line, "\t");
        return ls;
    }

    
    /**
     * Return a list of split String using Guava splitter
     * @param line
     * @param splitS
     * @return 
     */
    public static List<String> fastSplit (String line, String splitS) {
        List<String> ls = new ArrayList<>();
        Splitter spl = Splitter.on(splitS);
        for (String s : spl.split(line)) {
            ls.add(s);
        }
        return ls;
    }
    
    /**
     * Split string and return an array of string
     * @param line
     * @param splitS
     * @param startIndex
     * @param endIndex
     * @return 
     */
    public static List<String> fastSplit(String line, String splitS, int startIndex, int endIndex) {
        return fastSplit(line.substring(startIndex, endIndex), splitS);
    }
}
