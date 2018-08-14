package com.huya.marksman.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.huya.marksman.MarkApplication;
import com.huya.marksman.opengl.renders.PanoRenderer;
import com.huya.marksman.util.ScreenUtil;

/**
 * Created by charles on 2018/8/12.
 */

public class PanoSurfaceView extends GLSurfaceView implements SensorEventListener,
        View.OnTouchListener, GestureDetector.OnGestureListener {

    private boolean firstSensorCallback = true;
    private PanoRenderer renderer;
    private PanoImageListener imageListener;
    private boolean dragEnable = false;
    private boolean flingEnable = false;
    private int screenWidth;
    private int screenHeight;
    private ValueAnimator valueAnimatorX;
    private ValueAnimator valueAnimatorY;
    private GestureDetector gestureDetector;

    public PanoSurfaceView(Context context) {
        super(context);
        if (!isInEditMode()) {
            init();
        }
    }

    public PanoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent lastEvent, MotionEvent event, float distanceX, float distanceY) {
        valueAnimatorX.cancel();
        valueAnimatorY.cancel();
        float deltaX = distanceX / screenWidth * 90;
        float deltaY = distanceY / screenHeight * 90;
        renderer.addDragRotationX(-deltaX);
        renderer.addDragRotationY(deltaY);
        requestRender();
        if (imageListener != null) {
            imageListener.onRotationChanged(renderer.getRotationX(), renderer.getRotationY());
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if (!flingEnable) {
            return false;
        }

        if (Math.abs(velocityX) > Math.abs(velocityY)) {
            valueAnimatorX.cancel();
            valueAnimatorX.setInterpolator(new DecelerateInterpolator());
            valueAnimatorX.setFloatValues(velocityX / screenWidth / 1.5f, 0f);
            valueAnimatorX.setDuration(400);
            valueAnimatorX.removeAllUpdateListeners();
            valueAnimatorX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    renderer.addDragRotationX((Float) animation.getAnimatedValue());
                    requestRender();
                }
            });
            valueAnimatorX.start();
        }
        else {
            valueAnimatorY.cancel();
            valueAnimatorY.setInterpolator(new DecelerateInterpolator());
            valueAnimatorY.setFloatValues(-velocityY / screenHeight / 2.0f, 0f);
            valueAnimatorY.setDuration(400);
            valueAnimatorY.removeAllUpdateListeners();
            valueAnimatorY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    renderer.addDragRotationY((Float) animation.getAnimatedValue());
                    requestRender();
                }
            });
            valueAnimatorY.start();
        }
        return true;
    }

    public interface PanoImageListener {
        void onRotationChanged(float x, float y);
    }

    public void setInitRotation(int rotationX, int rotationY) {
        renderer.setInitRotation(rotationX, rotationY);
        firstSensorCallback = true;
        requestRender();

        if (imageListener != null) {
            imageListener.onRotationChanged(rotationX, rotationY);
        }
    }

    public void setImageListener(PanoImageListener imageListener) {
        this.imageListener = imageListener;
    }

    @Override
    public void onPause() {
        unregisterSensor();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerSensor();
    }

    private void registerSensor() {
        SensorManager sensorManager = (SensorManager) MarkApplication.getApplication().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (sensor != null) {
            firstSensorCallback = true;
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    private void unregisterSensor() {
        SensorManager sensorManager = (SensorManager) MarkApplication.getApplication().getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (sensor != null) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        if (type == Sensor.TYPE_ROTATION_VECTOR) {
            if (renderer != null) {
                Log.e("PanoSurfaceView", event.values.toString());
                renderer.setRotationVector(event.values, firstSensorCallback);
                requestRender();
                if (imageListener != null) {
                    imageListener.onRotationChanged(renderer.getRotationX(), renderer.getRotationY());
                }
            }
            firstSensorCallback = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!dragEnable) {
            return false;
        }
        return gestureDetector.onTouchEvent(event);
    }

    public void setDragEnable(boolean dragEnable) {
        this.dragEnable = dragEnable;
    }

    public void setFlingEnable(boolean flingEnable) {
        this.flingEnable = flingEnable;
    }


    private void init() {
        renderer = new PanoRenderer();
        setEGLContextClientVersion(2);
        setRenderer(renderer);
        setPreserveEGLContextOnPause(true);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setOnTouchListener(this);
        screenWidth = ScreenUtil.getScreenWidthPx();
        screenHeight = ScreenUtil.getScreenWidthPx();
        valueAnimatorX = new ValueAnimator();
        valueAnimatorY = new ValueAnimator();
        gestureDetector = new GestureDetector(getContext(), this);
    }
}
