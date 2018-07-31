package com.huya.marksman.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * Created by charles on 2018/7/30.
 */

public class PackageUtil {
    public static PackageInfo getPackageInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("PackageUtil", e.getLocalizedMessage());
        }
        return new PackageInfo();
    }
}
