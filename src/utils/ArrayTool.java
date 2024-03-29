package utils;

import java.util.Arrays;

/**
 * @author Daxing Xu
 */
public class ArrayTool {

    /**
     * 返回指定数值范围的随机数组（包含重复值）
     * @param arraySize 返回的数组大小
     * @param randomNumberOrigin  inclusive
     * @param randomNumberBound exclusive
     * @return 依据给定的数组范围返回一个随机数组（包含重复值), 如果给定的数值范围小于指定的数组大小, 则返回的数组大小为数值范围
     */
    public static int[] getRandomNumberArray(int arraySize, int randomNumberOrigin, int randomNumberBound){
        return new java.util.Random().ints(arraySize,randomNumberOrigin,randomNumberBound).toArray();
    }

    /**
     * 返回一个指定数值范围的随机数组（不包含重复值）
     * @param arraySize 返回的数组大小
     * @param randomNumberOrigin inclusive
     * @param randomNumberBound exclusive
     * @return 依据给定的数组范围返回一个随机数组（不含重复值), 如果给定的数值范围小于指定的数组大小, 则返回的数组大小为数值范围
     */
    public static int[] getRandomNonrepetitionArray(int arraySize, int randomNumberOrigin, int randomNumberBound){
        return new java.util.Random().ints(randomNumberOrigin, randomNumberBound).distinct().limit(arraySize).toArray();
    }

    /**
     *  将两个数组对应的index元素相加
     * @param a
     * @param b
     * @return
     */
    public static int[] add(int[] a, int[] b){
        if (a.length!=b.length){
            System.out.println(a+" and "+b+" length is not same");
            System.exit(1);
        }
        int[] c=new int[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i]=a[i]+b[i];
        }
        return c;
    }

    /**
     * 将两个数组对应的index元素相加
     * @param a
     * @param b
     * @return
     */
    public static double[] add(double[] a, double[] b){
        if (a.length!=b.length){
            System.out.println(Arrays.toString(a) +" and "+ Arrays.toString(b) +" length is not same");
            System.exit(1);
        }
        double[] c=new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i]=a[i]+b[i];
        }
        return c;
    }

}
