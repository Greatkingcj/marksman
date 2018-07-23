package com.huya.marksman.widget;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by charles on 2018/7/19.
 */

public class ShatterAnimGLView extends GLSurfaceView{

    public ShatterAnimGLView(Context context) {
        super(context);
    }

    public ShatterAnimGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initGLSurfaceView(Renderer renderer) {
        final ActivityManager activityManager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportEs2) {
            setEGLContextClientVersion(2);
            setEGLConfigChooser(8,8,8,8,16,0);
            setRenderer(renderer);
            getHolder().setFormat(PixelFormat.TRANSPARENT);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            setZOrderOnTop(true);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
