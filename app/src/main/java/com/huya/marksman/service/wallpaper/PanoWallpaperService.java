package com.huya.marksman.service.wallpaper;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.huya.marksman.widget.PanoSurfaceView;

import timber.log.Timber;

/**
 * Created by charles on 2018/8/12.
 */

public class PanoWallpaperService extends WallpaperService{

    private static Params sParams;

    static class Params {
        String path;
    }

    public static void setWallpaperWithImage(Activity activity, String path) {
        sParams = new Params();
        sParams.path = path;
        try {
            Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                    new ComponentName(activity, PanoWallpaperService.class));
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Timber.e("start wallpaper preview failed");
        }
    }


    @Override
    public Engine onCreateEngine() {
        return new PanoImageEngine();
    }

    class PanoImageEngine extends Engine {

        private EnginePanoImageView panoImageView;
        private boolean textureNeedUpdate = false;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            panoImageView = new EnginePanoImageView(PanoWallpaperService.this);
            panoImageView.setDragEnable(false);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            panoImageView.onTouch(panoImageView, event);
        }

        @Override
        public void onDestroy() {
            panoImageView.onWallpaperDestroy();
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                panoImageView.onResume();
                updateTexture();
            } else {
                panoImageView.onPause();
            }
        }

        private void updateTexture() {
            int rotationX = 0;
            int rotationY = 0;
            panoImageView.setInitRotation(rotationX, rotationY);
            //panoImageView.setImageFilePath(sParams.path);
        }

        class EnginePanoImageView extends PanoSurfaceView {

            public EnginePanoImageView(Context context) {
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
