package main.utils;

public class ChrPos implements Comparable<ChrPos>{

    String chr;
    int pos;

    public ChrPos(String chr, int pos){
        this.chr=chr;
        this.pos=pos;
    }

    public String getChr() {
        return chr;
    }

    public int getPos() {
        return pos;
    }

    @Override
    public String toString() {
        return "ChrPos{" +
                "chr='" + chr + '\'' +
                ", pos=" + pos +
                '}';
    }

    @Override
    public int compareTo(ChrPos o) {
        if (this.chr.equals(o.chr)){
            return Integer.compare(this.pos, o.pos);
        }else {
            return this.chr.compareTo(o.chr);
        }
    }
}
