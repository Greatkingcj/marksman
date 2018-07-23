package com.huya.huyaijkplayer.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

/**
 * Created by charles on 2018/3/20.
 */

public class MyCamera {
    private final static String TAG = "MyCamera";
    private Camera mCamera;
    private Camera.Parameters mCameraParameters;
    private Boolean running = false;

    public void start(SurfaceTexture surface) {
        mCamera = Camera.open(0);
        mCameraParameters = mCamera.getParameters();
        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
            running = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop() {
        if (running) {
            mCamera.stopPreview();
            mCamera.release();
            running = false;
        }
    }
}
