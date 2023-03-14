package main.utils;

import it.unimi.dsi.fastutil.bytes.Byte2CharArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ByteArrayMap;

/**
 * Class encoding alleles and site genotype from A, C, G, T, missing are not allowed in genotype
 * @author daxing
 */
public class AlleleEncoder {

    /**
     * Alleles in char
     */
    public static final char[] alleleBases = {'A', 'C', 'G', 'T'};

    /**
     * Alleles in byte code
     */
    public static final byte[] alleleBytes = {0, 1, 2, 3};


    /**
     * Converter from char to allele byte
     */
    public static final Char2ByteArrayMap alleleBaseToByteMap = new Char2ByteArrayMap(alleleBases, alleleBytes);

    /**
     * Converter from allele byte to char
     */
    public static final Byte2CharArrayMap alleleByteToBaseMap = new Byte2CharArrayMap(alleleBytes, alleleBases);

    /**
     * Return an allele byte from char
     * @param c, allele char
     * @return allele byte
     */
    public static byte getAlleleByteFromBase(char c) {
        return alleleBaseToByteMap.get(c);
    }

    /**
     * Return an allele char from byte
     * @param b, allele byte
     * @return allele char
     */
    public static char getAlleleBaseFromByte(byte b) {
        return alleleByteToBaseMap.get(b);
    }
}
