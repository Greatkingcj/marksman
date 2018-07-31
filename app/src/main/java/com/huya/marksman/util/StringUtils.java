package com.huya.marksman.util;

/**
 * Created by charles on 2018/7/30.
 */

public class StringUtils {
    public static boolean contain(String processname, String key) {
        return processname.contains(key);
    }

    public static boolean equals(String processname, String key) {
        return processname.equals(key);
    }
}
