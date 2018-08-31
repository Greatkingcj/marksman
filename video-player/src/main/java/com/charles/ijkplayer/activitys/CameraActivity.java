package com.charles.ijkplayer.activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.charles.ijkplayer.camera.MyCamera;
import com.charles.ijkplayer.camera.MyGLSurfaceView;

public class CameraActivity extends AppCompatActivity {

    private MyGLSurfaceView glSurfaceView;
    private MyCamera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        if(!hasPermission()){
            return;
        }
        mCamera = new MyCamera();
        glSurfaceView = new MyGLSurfaceView(this, mCamera);
        setContentView(glSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
        mCamera.stop();
    }

    private void requestPermission() {
        if (!hasPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private Boolean hasPermission() {
        Boolean hasPermission = true;
        //判断Android版本是否大于23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission =
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return hasPermission;
    }
}
