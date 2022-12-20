package org.mentpeak.common.util;

/**
 * 时间转换工具类
 */
public class TimeUtil {


    /**
     * 秒转为时分秒
     * @param seconds 秒
     * @return hh:mm:ss  7200 -> 02:00:00
     */
    public static String secondsToTimeHms(int seconds) {
        int hh = seconds / 3600;
        int mm = (seconds % 3600) / 60;
        int ss = (seconds % 3600) % 60;
        return (hh < 10 ? ("0" + hh) : hh) + ":" + (mm < 10 ? ("0" + mm) : mm) + ":" + (ss < 10 ? ("0" + ss) : ss);
    }

    /**
     * 秒转为时分秒
     * @param seconds 秒
     * @return 小时 分钟 秒
     */
    public static String secondsToTimeHourMinuteSecond(int seconds){
        int hh = seconds / 3600;
        int mm = (seconds % 3600) / 60;
        int ss = (seconds % 3600) % 60;

        return (hh < 10 ? ("0" + hh) : hh) + "小时" + (mm < 10 ? ("0" + mm) : mm) + "分钟" + (ss < 10 ? ("0" + ss) : ss) + "秒";
    }


    /**
     * 秒转为时分秒
     * @param seconds 秒
     * @return 小时 分钟 秒 || 分钟 秒
     */
    public static String secondsToTimeHourMinuteSecond2(int seconds){
        int hh = seconds / 3600;
        int mm = (seconds % 3600) / 60;
        int ss = (seconds % 3600) % 60;

        if(hh < 1  && mm < 1){
            return  (ss < 10 ? ("0" + ss) : ss) + "秒";
        } else if (hh < 1) {
            return  (mm < 10 ? ("0" + mm) : mm) + "分钟" + (ss < 10 ? ("0" + ss) : ss) + "秒";
        } else{
            return (hh < 10 ? ("0" + hh) : hh) + "小时" + (mm < 10 ? ("0" + mm) : mm) + "分钟" + (ss < 10 ? ("0" + ss) : ss) + "秒";
        }

    }

}
