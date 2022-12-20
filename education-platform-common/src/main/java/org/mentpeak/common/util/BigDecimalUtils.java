package org.mentpeak.common.util;

import java.math.BigDecimal;

/**
 * @Title BigDecimal比较大小的工具类
 * @Description //TODO
 * @Author LIMINGWEI
 * @Date 2021/1/7 13:21
 */
public class BigDecimalUtils {

    private BigDecimalUtils ( ) {

    }

    /**
     * 判断num1是否小于num2
     *
     * @param num1
     * @param num2
     * @return num1小于num2返回true
     */
    public static boolean lessThan (
            BigDecimal num1 ,
            BigDecimal num2 ) {
        return num1.compareTo ( num2 ) == - 1;
    }

    /**
     * 判断num1是否小于等于num2
     *
     * @param num1
     * @param num2
     * @return num1小于或者等于num2返回true
     */
    public static boolean lessEqual (
            BigDecimal num1 ,
            BigDecimal num2 ) {
        return ( num1.compareTo ( num2 ) == - 1 ) || ( num1.compareTo ( num2 ) == 0 );
    }

    /**
     * 判断num1是否大于num2
     *
     * @param num1
     * @param num2
     * @return num1大于num2返回true
     */
    public static boolean greaterThan (
            BigDecimal num1 ,
            BigDecimal num2 ) {
        return num1.compareTo ( num2 ) == 1;
    }

    /**
     * 判断num1是否大于等于num2
     *
     * @param num1
     * @param num2
     * @return num1大于或者等于num2返回true
     */
    public static boolean greaterEqual (
            BigDecimal num1 ,
            BigDecimal num2 ) {
        return ( num1.compareTo ( num2 ) == 1 ) || ( num1.compareTo ( num2 ) == 0 );
    }

    /**
     * 判断num1是否等于num2
     *
     * @param num1
     * @param num2
     * @return num1等于num2返回true
     */
    public static boolean equal (
            BigDecimal num1 ,
            BigDecimal num2 ) {
        return num1.compareTo ( num2 ) == 0;
    }


    public static void main ( String[] args ) {
        BigDecimal b1 = BigDecimal.valueOf ( 0.3 );
        BigDecimal b2 = BigDecimal.valueOf ( 0.3 );
        System.out.println ( b1 + "------" + b2 );
        boolean result = BigDecimalUtils.greaterEqual ( b1 ,
                                                        b2 );
        System.out.println ( result );
    }
}
