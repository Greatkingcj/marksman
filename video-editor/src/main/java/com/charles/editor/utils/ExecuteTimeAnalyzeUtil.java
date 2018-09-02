package com.charles.editor.utils;

import android.util.Log;

/**
 * author  :  hch
 * date    :  2017/11/30.
 * comment : 耗时助手
 */

public class ExecuteTimeAnalyzeUtil {

    private static long startTime;

    private static final boolean ANALYZEOPEN = true;

    private static String preTag = "";

    private static String TAG = "ExecuteTimeAnalyzeUtil";

    public static void start(){
        if (ANALYZEOPEN) {
            startTime = System.currentTimeMillis();
            Log.i(TAG, "ayalyze started at ------------> " + startTime);
        }
    }

    public static void tag(String tag) {
        if (ANALYZEOPEN) {

            if (preTag.equals("")) {
                Log.i(TAG, "execute from start to ["+tag+"] time used ------------> " + (System.currentTimeMillis() - startTime));
            }else{
                Log.i(TAG, "execute from ["+preTag+"] to ["+tag+"] time used ------------> " + (System.currentTimeMillis() - startTime));
            }
            preTag = tag;
            startTime = System.currentTimeMillis();
        }
    }

    public static void tag() {
        if (ANALYZEOPEN) {
            Log.i(TAG, "util now  time used ------------> " + (System.currentTimeMillis() - startTime));
            startTime = System.currentTimeMillis();
        }
    }

    public static void end() {
        preTag = "";
        startTime = 0;
    }

}
