package org.mentpeak.common.util;

import java.text.NumberFormat;

/**
 * 计算工具类
 */
public class CalculationUtil {


    /**
     * 求 百分比
     *
     * @param num1     分子
     * @param num2     分母
     * @param accurate 精确位
     * @return
     */
    public static String getPercentage (
            int num1 ,
            int num2 ,
            int accurate ) {

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance ( );

        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits ( accurate );

        String result = numberFormat.format ( ( float ) num1 / ( float ) num2 * 100 );

        return result;

    }


    /**
     * 求 小数
     *
     * @param num1     分子
     * @param num2     分母
     * @param accurate 精确位
     * @return
     */
    public static String getXiaoshu (
            int num1 ,
            int num2 ,
            int accurate ) {

        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance ( );

        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits ( accurate );

        String result = numberFormat.format ( ( float ) num1 / ( float ) num2 );

        return result;

    }


    public static void main ( String[] args ) {

        int num1 = 2;

        int num2 = 60;

//        String percentage = getXiaoshu(num1, num2, 2);

        String percentage = getXiaoshu ( 0 ,
                                         60 ,
                                         2 );

        System.out.println ( "num1和num2的小数为:" + percentage );

//        System.out.println("num1和num2的百分比为:" + percentage + "%");
    }
}
