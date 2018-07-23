package com.huya.marksman;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huya.marksman.renders.ParticlesRenderer;
import com.huya.marksman.renders.StarryRenderer;
import com.huya.marksman.ui.AirHockeyActivity;
import com.huya.marksman.ui.ParticlesActivity;
import com.huya.marksman.ui.ShatterAnimActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author charles
 */
public class MainActivity extends AppCompatActivity {
    @Bind(R.id.gl_surface_view) GLSurfaceView glSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
        if (supportsEs2) {
            glSurfaceView.setEGLContextClientVersion(2);
            StarryRenderer starryRenderer = new StarryRenderer(glSurfaceView, this);
            glSurfaceView.setRenderer(starryRenderer);
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    public void gotoFirst(View view) {
        startActivity(new Intent(this, AirHockeyActivity.class));
    }

    public void  gotoSecond(View view) {
        startActivity(new Intent(this, ParticlesActivity.class));
    }

    public void  gotoThird(View view) {
        startActivity(new Intent(this, ShatterAnimActivity.class));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}