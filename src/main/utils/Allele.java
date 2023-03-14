package main.utils;

/**
 * Class holding allele and its basic information
 * Basic information are stored in bits
 * @author feilu
 */
public class Allele {
    byte baseVal = -1;

    /**
     * Construct an object of {@link Allele}
     * @param c
     */
    public Allele(char c) {
        this.baseVal = AlleleEncoder.getAlleleByteFromBase(c);
    }

    /**
     * Return the byte code of the allele, see {@link AlleleEncoder}
     * @return
     */
    public byte getAlleleByte () {
        return this.baseVal;
    }

}
