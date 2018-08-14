package com.huya.marksman.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.huya.marksman.listener.ShatterAnimListener;
import com.huya.marksman.opengl.renders.ShatterAnimRender;

/**
 *
 * @author charles
 * @date 2018/7/19
 */

public class ShatterAnimLayout extends FrameLayout{

    private ShatterAnimGLView shatterAnimGLView;
    private ShatterAnimRender shatterAnimRender;

    public ShatterAnimLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ShatterAnimLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(shatterAnimGLView);
    }

    private void initView() {
        shatterAnimGLView = new ShatterAnimGLView(getContext());
        shatterAnimRender = new ShatterAnimRender(shatterAnimGLView, getContext());
        shatterAnimGLView.initGLSurfaceView(shatterAnimRender);
        shatterAnimRender.setShatterAnimListener(new ShatterAnimListener() {
            @Override
            public void onAnimStart() {

            }

            @Override
            public void onAnimFinish() {
                getHandler().post(() -> {
                    for (int i = 0; i < getChildCount(); i++) {
                        if (getChildAt(i) != shatterAnimGLView) {
                            getChildAt(i).setVisibility(VISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void startAnimation() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        super.draw(canvas);
        shatterAnimRender.startAnimation(bitmap);

        getHandler().post(() -> {
            for (int i = 0; i < getChildCount(); i++) {
                if (getChildAt(i) != shatterAnimGLView) {
                    getChildAt(i).setVisibility(GONE);
                }
            }
        });
    }

    public void stopAnimation() {
        shatterAnimRender.stopAnimation();
    }

    public void onResume() {
        if (shatterAnimGLView != null) {
            shatterAnimGLView.onResume();
        }
    }

    public void onPause() {
        if (shatterAnimGLView != null) {
            shatterAnimGLView.onPause();
        }
    }

    public void onDestroy() {
        if (shatterAnimRender != null) {
            shatterAnimRender.destroy();
        }
    }

}
