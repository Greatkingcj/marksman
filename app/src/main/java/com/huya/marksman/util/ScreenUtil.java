package com.huya.marksman.util;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


import com.huya.marksman.MarkApplication;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenUtil {

    /**
     * judge whether current device has a small screen (width shorter than 500 pixels or height shorter than 900 pixels)
     * @return true if current device's screen is small
     */
    public static boolean isSmallScreen() {
        DisplayMetrics metrics = MarkApplication.getApplication().getResources().getDisplayMetrics();
        if (metrics.widthPixels < 500 || metrics.heightPixels < 900) {
            return true;
        }
        return false;
    }

    public static void getRawScreenDimen(int[] dimens) {
        int width = 0, height = 0;
        final DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) MarkApplication.getApplication().
                getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Method mGetRawH = null, mGetRawW = null;

        try {
            // For JellyBean 4.2 (API 17) and onward
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(metrics);

                width = metrics.widthPixels;
                height = metrics.heightPixels;
            } else {
                mGetRawH = Display.class.getMethod("getRawHeight");
                mGetRawW = Display.class.getMethod("getRawWidth");

                try {
                    width = (Integer) mGetRawW.invoke(display);
                    height = (Integer) mGetRawH.invoke(display);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        }

        dimens[0] = width;
        dimens[1] = height;
    }

    public static int getRawScreenWidth() {
        int[] dimens = new int[2];
        getRawScreenDimen(dimens);
        return dimens[0];
    }

    public static int getRawScreenHeight() {
        int[] dimens = new int[2];
        getRawScreenDimen(dimens);
        return dimens[1];
    }

    public static int getScreenWidthPx() {
        DisplayMetrics metrics = MarkApplication.getApplication().getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeightPx() {
        DisplayMetrics metrics = MarkApplication.getApplication().getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int px2dp(float px) {
        float density = MarkApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (px / density + 0.5f);
    }

    public static int dp2px(float dp) {
        float density = MarkApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int getScreenWidthDp() {
        float width = MarkApplication.getApplication().getResources().getDisplayMetrics().widthPixels;
        return px2dp(width);

    }

    public static int getScreenHeightDp() {
        float height = MarkApplication.getApplication().getResources().getDisplayMetrics().heightPixels;
        return px2dp(height);
    }

    public static int sp2px(float spValue) {
        final float fontScale = MarkApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
