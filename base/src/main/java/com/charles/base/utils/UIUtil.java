package com.charles.base.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.design.widget.TabLayout;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;

import java.lang.reflect.Field;


/**
 * Created by niegangfeng on 2018/05/07.
 */

public class UIUtil {
    public static void addClickEffectForView(View... views) {
        for (int i = 0; i < views.length; i++) {
            PropertyValuesHolder valueHolder_in_x = PropertyValuesHolder.ofFloat(
                    "scaleX", 1f, 0.9f);
            PropertyValuesHolder valuesHolder_in_y = PropertyValuesHolder.ofFloat(
                    "scaleY", 1f, 0.9f);
            final Animator animIn = ObjectAnimator.ofPropertyValuesHolder(views[i], valueHolder_in_x,
                    valuesHolder_in_y);
            animIn.setDuration(100);
            animIn.setInterpolator(new LinearInterpolator());

            PropertyValuesHolder valueHolder_out_x = PropertyValuesHolder.ofFloat(
                    "scaleX", 0.9f, 1f);
            PropertyValuesHolder valuesHolder_out_y = PropertyValuesHolder.ofFloat(
                    "scaleY", 0.9f, 1f);
            final Animator animOut = ObjectAnimator.ofPropertyValuesHolder(views[i], valueHolder_out_x,
                    valuesHolder_out_y);
            animOut.setDuration(100);
            animOut.setInterpolator(new LinearInterpolator());
            views[i].setOnTouchListener(new View.OnTouchListener() {
                boolean cancelled;
                Rect rect = new Rect();

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            animIn.start();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (rect.isEmpty()) {
                                v.getDrawingRect(rect);
                            }
                            if (!cancelled && !rect.contains((int) event.getX(), (int) event.getY())) {
                                animOut.start();
                                cancelled = true;
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            if (!cancelled) {
                                animOut.start();
                            } else {
                                cancelled = false;
                            }
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    public static void addMinifyEffectForView(View view) {
        PropertyValuesHolder valueHolder_in_x = PropertyValuesHolder.ofFloat(
                "scaleX", 1f, 0.9f);
        PropertyValuesHolder valuesHolder_in_y = PropertyValuesHolder.ofFloat(
                "scaleY", 1f, 0.9f);
        PropertyValuesHolder valueHolder_in_x_2 = PropertyValuesHolder.ofFloat(
                "scaleX", 0.9f, 0.95f);
        PropertyValuesHolder valuesHolder_in_y_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 0.9f, 0.95f);
        final Animator animIn = ObjectAnimator.ofPropertyValuesHolder(view, valueHolder_in_x,
                valuesHolder_in_y, valueHolder_in_x_2, valuesHolder_in_y_2);
        animIn.setDuration(100);
        animIn.setInterpolator(new LinearInterpolator());
        animIn.start();
    }

    public static void addEnlargeEffectForView(View view) {
        PropertyValuesHolder valueHolder_out_x_2 = PropertyValuesHolder.ofFloat(
                "scaleX", 0.95f, 0.9f);
        PropertyValuesHolder valuesHolder_out_y_2 = PropertyValuesHolder.ofFloat(
                "scaleY", 0.95f, 0.9f);
        PropertyValuesHolder valueHolder_out_x = PropertyValuesHolder.ofFloat(
                "scaleX", 0.9f, 1f);
        PropertyValuesHolder valuesHolder_out_y = PropertyValuesHolder.ofFloat(
                "scaleY", 0.9f, 1f);
        final Animator animOut = ObjectAnimator.ofPropertyValuesHolder(view, valueHolder_out_x_2, valuesHolder_out_y_2,
                valueHolder_out_x, valuesHolder_out_y);
        animOut.setDuration(100);
        animOut.setInterpolator(new LinearInterpolator());
        animOut.start();
    }


    private static final long CLICK_INTERVAL = 1000;
    private static long sLastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long currentClickTime = System.currentTimeMillis();
        if (currentClickTime - sLastClickTime < CLICK_INTERVAL) {
            flag = true;
        }
        sLastClickTime = currentClickTime;
        return flag;
    }

    public static void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }
    }
}
