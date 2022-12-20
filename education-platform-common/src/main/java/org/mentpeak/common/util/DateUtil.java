package org.mentpeak.common.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;

public class DateUtil {


    /**
     * 结束时间
     *
     * @param date
     * @return 2021-02-01 23:59:59
     */
    public static Date getEndOfDay ( Date date ) {
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant ( Instant.ofEpochMilli ( date.getTime ( ) ) ,
                                          ZoneId.systemDefault ( ) );
        LocalDateTime endOfDay = localDateTime.with ( LocalTime.MAX );
        return Date.from ( endOfDay.atZone ( ZoneId.systemDefault ( ) )
                                   .toInstant ( ) );
    }

    /**
     * 开始时间
     *
     * @param date
     * @return 2021-02-01 00:00:00
     */
    public static Date getStartOfDay ( Date date ) {
        LocalDateTime localDateTime =
                LocalDateTime.ofInstant ( Instant.ofEpochMilli ( date.getTime ( ) ) ,
                                          ZoneId.systemDefault ( ) );
        LocalDateTime startOfDay = localDateTime.with ( LocalTime.MIN );
        return Date.from ( startOfDay.atZone ( ZoneId.systemDefault ( ) )
                                     .toInstant ( ) );
    }


    /**
     * 自定义 开始时间
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date mydefineParseStartOfDay (
            String dateStr ,
            String pattern ) {

        SimpleDateFormat sf = new SimpleDateFormat ( pattern );

        try {
            Date parse = sf.parse ( dateStr );
            return getStartOfDay ( parse );
        } catch ( ParseException e ) {
            e.printStackTrace ( );
        }
        return null;
    }

    /**
     * 自定义 结束时间
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date mydefineParseEndOfDay (
            String dateStr ,
            String pattern ) {
        SimpleDateFormat sf = new SimpleDateFormat ( pattern );

        try {
            Date parse = sf.parse ( dateStr );
            return getEndOfDay ( parse );
        } catch ( ParseException e ) {
            e.printStackTrace ( );
        }
        return null;
    }

    public static LocalDateTime solveFrontEndTooLazy ( String date ) {
        //前端传来的值不给传需要格式，这里自己格式化
        DateTimeFormatter df = DateTimeFormatter.ofPattern ( "yyyy-MM-dd HH:mm:ss" );
        LocalDateTime parse = LocalDateTime.parse ( date ,
                                                    df );
        return parse;
    }

    /**
     * 将字符串时间转换为LocalDateTime,时间格式yyyy-MM-dd
     *
     * @param time
     * @return
     */
    public static LocalDateTime parseStringToDateTime ( String time ) {
        DateTimeFormatter df = new DateTimeFormatterBuilder ( )
                .appendPattern ( "yyyy-MM-dd[['T'hh][:mm][:ss]]" )
                .parseDefaulting ( ChronoField.HOUR_OF_DAY ,
                                   0 )
                .parseDefaulting ( ChronoField.MINUTE_OF_HOUR ,
                                   0 )
                .parseDefaulting ( ChronoField.SECOND_OF_MINUTE ,
                                   0 )
                .parseDefaulting ( ChronoField.MILLI_OF_SECOND ,
                                   0 )
                .toFormatter ( );
        LocalDateTime parse = LocalDateTime.parse ( time ,
                                                    df );
        return parse;
    }

    /**
     * 对LocalDateTime进行指定格式的格式换并转字符串
     *
     * @param datetime
     * @param pattern
     * @return
     */
    public static String format (
            LocalDateTime datetime ,
            String pattern ) {
        return DateTimeFormatter.ofPattern ( pattern )
                                .format ( datetime );
    }

    /**
     * LocalDateTime 转为字符串
     *
     * @param datetime 2021-04-13T18:40:26.883
     * @return 2021-04-13
     */
    public static String LocalDateTimeToString ( LocalDateTime datetime ) {
        String pattern = "yyyy-MM-dd";
        return DateTimeFormatter.ofPattern ( pattern )
                                .format ( datetime );
    }


    /**
     * LocalDateTime 转为字符串时间
     *
     * @param datetime 2021-04-13T18:40:26.883
     * @return 2021-04-13 00:00:00
     */
    public static String LocalDateTimeToStringTime ( LocalDateTime datetime ) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return DateTimeFormatter.ofPattern ( pattern )
                .format ( datetime );
    }



    /**
     * 根据当前时间 返回 上半年 以及下半年的时间
     * @return
     */
    public static String[] getTime(){

        int year = LocalDateTime.now().getYear();
        int month = LocalDateTime.now().getMonthValue();

        String[] arr = new String[2];

        if(month >= 1 && month <= 6){
            // 上半年
            arr[0] = year + ":01:01 00:00:00";
            arr[1] = year + ":06:30 23:59:59";
        }
        if(month >= 6  && month <= 12){
            // 下半年
            arr[0] = year + ":07:01 00:00:00";
            arr[1] = year + ":12:31 23:59:59";
        }

        return arr;
    }



    public static void main ( String[] args ) {

        SimpleDateFormat sf = new SimpleDateFormat ( "yyyy-MM-dd" );
        SimpleDateFormat sdf = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss" );

        Date parse = null;
        try {
            parse = sf.parse ( "2021-02-01" );
        } catch ( ParseException e ) {
            e.printStackTrace ( );
        }

        Date startOfDay = getStartOfDay ( parse );
        Date endOfDay = getEndOfDay ( parse );
        String starttime = sdf.format ( startOfDay );
        String endtime = sdf.format ( endOfDay );

        System.out.printf ( "开始" + starttime + "结束" + endtime );

    }

}
