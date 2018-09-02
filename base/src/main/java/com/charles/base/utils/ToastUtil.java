package com.charles.base.utils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.widget.Toast;

import com.charles.base.utils.compat.ToastCompat;


/**
 * Created by smf on 2017/10/12.
 */

public class ToastUtil {
    /**
     * 判断当前线程，合理显示Toast
     *
     * @param context  上下文
     * @param resStrId 字符串的id
     * @param duration How long to display the message.  Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void show(final Context context, @StringRes final int resStrId, final int duration) {
        if (context == null) {
            return;
        }

        String content = context.getResources().getString(resStrId);
        if (TextUtils.isEmpty(content)) {
            return;
        }
        show(context, content, duration);
    }

    /**
     * 判断当前线程，合理显示Toast
     *
     * @param context  上下文
     * @param content  显示的内容
     * @param duration How long to display the message.  Either {@link Toast#LENGTH_SHORT} or
     *                 {@link Toast#LENGTH_LONG}
     */
    public static void show(final Context context, @NonNull final CharSequence content, final int duration) {
        if (context == null || TextUtils.isEmpty(content)) {
            return;
        }

        // 判断是否为主线程
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (checkIfNeedToCompact()) {
                ToastCompat.makeText(context, content, duration).show();
            } else {
                Toast.makeText(context, content, duration).show();
            }
        } else {
            // 如果不是，就用该方法使其在ui线程中运行
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (checkIfNeedToCompact()) {
                        ToastCompat.makeText(context, content, duration).show();
                    } else {
                        Toast.makeText(context, content, duration).show();
                    }
                }
            });
        }
    }

    private static boolean checkIfNeedToCompact() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1;
    }
}
