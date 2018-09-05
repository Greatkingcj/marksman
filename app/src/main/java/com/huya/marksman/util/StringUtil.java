package com.huya.marksman.util;

import android.support.annotation.NonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by niegangfeng on 2017/8/31.
 */

public class StringUtil {
    // (?:youtube(?:-nocookie)?\.com\/(?:[^\/\n\s]+\/\S+\/|(?:v|e(?:mbed)?)\/|\S*?[?&]v=)|youtu\.be\/)([a-zA-Z0-9_-]{11})
    private final static String youtubeReg = "(?:youtube(?:-nocookie)?\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})";

    public static boolean isNull(String temp) {
        if (temp == null || temp.trim().length() == 0) {
            return true;
        }
        return false;
    }
    // 判断字符个数
    public static int length(CharSequence s) {
        if (s == null || "".equals(s)) {
            return 0;
        }
        int counter = 0;
        String regEx = "[\u4E00-\u9FA5]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher;
        for (int i = 0; i < s.length(); i++) {
            matcher = pattern.matcher(s.charAt(i) + "");
            if (matcher.matches()) {
                counter += 2;
            } else {
                counter++;
            }
        }
        return counter;
    }

    /**
     * 去掉换行符、制表符
     * @param str
     * @return
     */
    public static String removeNewLine(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String getWholeNum(String code, String num) {
        StringBuilder sb = new StringBuilder();
        String pref = "";
        if ("+86".equals(code)) {
            pref = code.replace("+", "0");
        } else {
            pref = code.replace("+", "00");
        }
        sb.append(pref);
        sb.append(num);
        return sb.toString();
    }

    public static String getVideoId(@NonNull String videoUrl) {
        Pattern pattern = Pattern.compile(youtubeReg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(videoUrl);

        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public static String formatArticleUrl(String host, long articleId) {
        String articleUrl = String.format("http://%1$s/box/article/app/%2$d.html", host, articleId);
        return articleUrl;
    }
}
