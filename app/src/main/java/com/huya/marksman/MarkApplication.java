package com.huya.marksman;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.huya.marksman.util.PackageUtil;
import com.huya.marksman.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/**
 * Created by charles on 2018/7/30.
 */

public class MarkApplication extends Application{
    private static final String TAG = "MarkApplication";
    public static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.e(TAG, "loadDex " + "App attachBaseContext");
        if (!quickStart() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (needWait(base)) {
                waitForDexopt(base);
            }
        } else {
            return;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (quickStart()) {
            return;
        }
    }

    private void waitForDexopt(Context base) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(
            "com.huya.marksman", LoadDexActivity.class.getName()
        );
        intent.setComponent(componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        base.startActivity(intent);
        long startWait = System.currentTimeMillis();
        long waitTime = 10 * 1000;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            waitTime = 20 * 1000;
        }
        while (needWait(base)) {
            try {
                long nowWait = System.currentTimeMillis() - startWait;
                Log.e(TAG, "wait ms: " + nowWait);
                if (nowWait >= waitTime) {
                    return;
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean needWait(Context base) {
        String flag = get2thDexSHA1(base);
        Log.e(TAG, "pid: " + "dex-sha1" + flag);
        SharedPreferences sp = base.getSharedPreferences(
                PackageUtil.getPackageInfo(base).versionName, MODE_MULTI_PROCESS
        );
        String savaValue = sp.getString(KEY_DEX2_SHA1, "");
        return !StringUtils.equals(flag, savaValue);
    }

    private String get2thDexSHA1(Context base) {
        ApplicationInfo applicationInfo = base.getApplicationInfo();
        String source = applicationInfo.sourceDir;
        try {
            JarFile jarFile = new JarFile(source);
            Manifest manifest = jarFile.getManifest();
            Map<String, Attributes> map = manifest.getEntries();
            Attributes attributes = map.get("classes2.dex");
            return attributes.getValue("SHA1-Digest");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean quickStart() {
        if (StringUtils.contain(getCurrentProcessName(this), ":mini")) {
            Log.e(TAG, "pid: " + "mini start!");
            return true;
        }
        return false;
    }

    private static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        Log.e(TAG, "pid: " + pid);
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()) {
            if (appProcessInfo.pid == pid) {
                return appProcessInfo.processName;
            }
        }
        return null;
    }


    public void installFinish(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                PackageUtil.getPackageInfo(context).versionName, MODE_MULTI_PROCESS);
        sp.edit().putString(KEY_DEX2_SHA1,get2thDexSHA1(context)).commit();
    }

}
