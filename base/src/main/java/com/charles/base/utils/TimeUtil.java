package com.charles.base.utils;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by hch on 2018/6/5.
 */

public class TimeUtil {

    // 格式： 年-月-日
    public static ThreadLocal<DateFormat> dateFormat1 = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        }
    };

    // 格式： 年-月-日 小时:分钟:秒
    public static ThreadLocal<DateFormat> dateFormat2 = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        }
    };

    /**
     * 将时间戳转换为时间
     */
    public static String stampToDate(long timeMillis){
        return dateFormat1.get().format(timeMillis);
    }

    public static String convertSecondsToTime(long seconds) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (seconds <= 0)
            return "00:00";
        else {
            minute = (int)seconds / 60;
            if (minute < 60) {
                second = (int)seconds % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = (int)(seconds - hour * 3600 - minute * 60);
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    private static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

}
