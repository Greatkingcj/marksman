package com.huya.marksman.service.wallpaper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.huya.marksman.opengl.renders.ParticlesRenderer;

/**
 * Created by charles on 2018/7/11.
 */

public class GLWallpaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new GLEngine();
    }

    public class GLEngine extends Engine {
        private WallpaperGLSurfaceView glSurfaceView;
        private boolean rendererSet;
        ParticlesRenderer particlesRenderer;
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);

            ActivityManager activityManager =
                    (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            ConfigurationInfo configurationInfo = activityManager
                    .getDeviceConfigurationInfo();
            final boolean supportsEs2 =
                    configurationInfo.reqGlEsVersion >= 0x20000;

            particlesRenderer =
                    new ParticlesRenderer(GLWallpaperService.this);

            if (supportsEs2) {
                glSurfaceView.setEGLContextClientVersion(2);
                glSurfaceView.setRenderer(particlesRenderer);
                rendererSet = true;
            } else {
                Toast.makeText(GLWallpaperService.this, "not support gles 2.0", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (rendererSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            glSurfaceView.onWallpaperDestroy();
        }

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            glSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    particlesRenderer.handleOffsetsChanged(xOffset, yOffset);
                }
            });
        }

        class WallpaperGLSurfaceView extends GLSurfaceView {

            public WallpaperGLSurfaceView(Context context) {
                super(context);
            }

            @Override
            public SurfaceHolder getHolder() {
                return getSurfaceHolder();
            }

            public void onWallpaperDestroy() {
                super.onDetachedFromWindow();
            }
        }
    }
}
