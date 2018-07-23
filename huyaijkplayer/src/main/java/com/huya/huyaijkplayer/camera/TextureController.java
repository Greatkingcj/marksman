package com.huya.huyaijkplayer.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.huya.huyaijkplayer.filter.AFilter;
import com.huya.huyaijkplayer.filter.GroupFilter;
import com.huya.huyaijkplayer.filter.NoFilter;
import com.huya.huyaijkplayer.utils.EasyGlUtils;
import com.huya.huyaijkplayer.utils.MatrixUtils;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by charles on 2018/3/21.
 * 使用GLSurfaceView创建GL环境，同时让这个环境为离屏渲染服务
 * 而不是直接渲染到GLSurfaceView的Surface上
 */

public class TextureController implements GLSurfaceView.Renderer{

    private static final String TAG = "TextureController";

    private Object surface;
    private GLView mGLView;
    private Context mContext;

    private Renderer mRenderer;

    //特效处理filter
    private TextureFilter mFilter;
    //用来渲染输出的Filter
    private AFilter mShowFilter;
    //中间特效
    private GroupFilter mGroupFilter;

    //数据大小
    private Point mDataSize;
    //输出视图的大小
    private Point mWindowSize;
    //AiyaFilter方向flag
    private int mDirectionFlag = -1;

    //回调以及回调数据的宽高，用于提供给外界使用（提供处理完后的数据给外界的接口）
    private FrameCallback mFrameCallback;
    private int frameCallbackWidth, frameCallbackHeight;
    //用于存储回调数据的buffer
    private ByteBuffer[] outPutBuffer = new ByteBuffer[3];
    //回调数据使用的buffer索引
    private int indexOutput = 0;
    //用于绘制回调缩放的矩阵
    private float[] callbackOM = new float[16];

    //创建离屏buffer，用于最后导出数据
    private int[] mExportFrame = new int[1];
    private int[] mExportTexture = new int[1];

    //用于绘制到屏幕上的变换矩阵
    private float[] SM = new float[16];
    //输出到屏幕上的方式
    private int mShowType = MatrixUtils.TYPE_CENTERCROP;

    private AtomicBoolean isParamSet = new AtomicBoolean(false);

    //拍摄flag
    private boolean isShoot = false;


    public TextureController(Context context) {
        this.mContext = context;
        init();
    }

    public void surfaceCreated(Object nativeWindow) {
        this.surface = nativeWindow;
        mGLView.surfaceCreated(null);
    }

    public void surfaceChanged(int width, int height) {
        this.mWindowSize.x = width;
        this.mWindowSize.y = height;
        mGLView.surfaceChanged(null, 0, width, height);
    }

    public void surfaceDestroyed() {
        mGLView.surfaceDestroyed(null);
    }

    public void takePhoto() {
        isShoot = true;
    }

    public void setFrameCallback(int width, int height, FrameCallback frameCallback) {
        this.frameCallbackWidth = width;
        this.frameCallbackHeight = height;
        if (frameCallbackWidth > 0 && frameCallbackHeight > 0) {
            if (outPutBuffer != null) {
                outPutBuffer = new ByteBuffer[3];
            }
            calculateCallbackOM();
            this.mFrameCallback = frameCallback;
        } else {
            this.mFrameCallback = null;
        }
    }

    private void calculateCallbackOM() {
        if (frameCallbackWidth > 0 && frameCallbackHeight > 0
                && mDataSize.x > 0 && mDataSize.y > 0) {
            MatrixUtils.getMatrix(callbackOM, MatrixUtils.TYPE_CENTERCROP,
                    mDataSize.x, mDataSize.y,
                    frameCallbackWidth, frameCallbackHeight);
            MatrixUtils.flip(callbackOM, false, true);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        Log.d(TAG, "TextureController:onSurfaceCreated");

        mFilter.create();
        mGroupFilter.create();
        mShowFilter.create();
        if (!isParamSet.get()) {
            if (mRenderer != null) {
                mRenderer.onSurfaceCreated(gl, config);
            }
            sdkParamSet();
        }

        calculateCallbackOM();
        mFilter.setFlag(mDirectionFlag);

        deleteFrameBuffer();
        GLES20.glGenFramebuffers(1, mExportFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, mExportTexture, 0,
                GLES20.GL_RGBA, mDataSize.x, mDataSize.y);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        Log.d(TAG, "TextureController:onSurfaceChanged");

        MatrixUtils.getMatrix(SM, mShowType, mDataSize.x,
                mDataSize.y, width, height);
        mShowFilter.setSize(width, height);
        mShowFilter.setMatrix(SM);
        mGroupFilter.setSize(mDataSize.x, mDataSize.y);
        mShowFilter.setSize(mDataSize.x, mDataSize.y);
        mFilter.setSize(mDataSize.x, mDataSize.y);
        if (mRenderer != null) {
            mRenderer.onSurfaceChanged(gl, width, height);
        }

    }

    @Override
    public void onDrawFrame(GL10 gl) {

        Log.d(TAG, "TextureController:onDrawFrame");

        if (isParamSet.get()) {
            mFilter.draw();
            mGroupFilter.setTextureId(mFilter.getOutputTexture());
            mGroupFilter.draw();

            //显示传入的texture上， 一般是显示在屏幕上
            GLES20.glViewport(0, 0, mWindowSize.x, mWindowSize.y);
            mShowFilter.setMatrix(SM);
            mShowFilter.setTextureId(mGroupFilter.getOutputTexture());
            mShowFilter.draw();

            if (mRenderer != null) {
                mRenderer.onDrawFrame(gl);
            }

            callbackIfNeeded();
        }
    }

    private void init() {
        mGLView = new GLView(mContext);

        //避免GLView的attachToWindow和detachFromWindow崩溃
        ViewGroup v = new ViewGroup(mContext) {
            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {

            }
        };
        v.addView(mGLView);
        v.setVisibility(View.GONE);

        mFilter = new TextureFilter(mContext.getResources());
        mShowFilter = new NoFilter(mContext.getResources());
        mGroupFilter = new GroupFilter(mContext.getResources());

        //初始化数据源图像宽高以及窗口宽高
        mDataSize = new Point(720, 1280);
        mWindowSize = new Point(720, 1280);
    }

    /**
     * 在Surface创建前，应该被调用
     * @param width
     * @param height
     */
    public void setDataSize(int width, int height) {
        mDataSize.x = width;
        mDataSize.y = height;
    }

    public SurfaceTexture getTexture() {
        return mFilter.getTexture();
    }

    public void setImageDirection(int flag) {
        this.mDirectionFlag = flag;
    }

    public void setRenderer(Renderer renderer) {
        mRenderer = renderer;
    }

    public void requestRender() {
        mGLView.requestRender();
    }

    public void create(int width, int height) {
        mGLView.attachedToWindow();
        surfaceCreated(surface);
        surfaceChanged(width, height);
    }

    public void destroy() {
        if (mRenderer != null) {
            mRenderer.onDestroy();
        }
        mGLView.surfaceDestroyed(null);
        mGLView.detachedFromWindow();
        mGLView.clear();
    }

    public void onPause() {
        mGLView.onPause();
    }

    public void onResume() {
        mGLView.onResume();
    }

    private void sdkParamSet(){
        if (!isParamSet.get() && mDataSize.x > 0 &&
                mDataSize.y > 0) {
            isParamSet.set(true);
        }
    }

    private void deleteFrameBuffer() {
        GLES20.glDeleteFramebuffers(1, mExportFrame, 0);
        GLES20.glDeleteTextures(1, mExportTexture, 0);
    }

    /**
     * 需要回调，则缩放图片到指定大小，读取数据并回调
     */
    private void callbackIfNeeded() {
        if (mFrameCallback != null && isShoot) {
            indexOutput = indexOutput++ >= 2 ? 0 : indexOutput;
            if (outPutBuffer[indexOutput] == null) {
                outPutBuffer[indexOutput] = ByteBuffer.allocate(frameCallbackWidth *
                        frameCallbackHeight*4);
            }
            GLES20.glViewport(0, 0, frameCallbackWidth, frameCallbackHeight);
            EasyGlUtils.bindFrameTexture(mExportFrame[0], mExportTexture[0]);
            mShowFilter.setMatrix(callbackOM);
            mShowFilter.draw();
            frameCallback();
            isShoot = false;
            EasyGlUtils.unBindFrameBuffer();
            mShowFilter.setMatrix(SM);
        }
    }

    private void frameCallback() {
        GLES20.glReadPixels(0, 0, frameCallbackWidth, frameCallbackHeight,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, outPutBuffer[indexOutput]);
        mFrameCallback.onFrame(outPutBuffer[indexOutput].array(), 0);
    }

    private class GLView extends GLSurfaceView {

        public GLView(Context context) {
            super(context);
            init();
        }

        public GLView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        private void init() {

            //避免GLSurfaceVIew自带的Surface影响渲染
            getHolder().addCallback(null);

            //指定外部传入的surface作为渲染的window surface
            setEGLWindowSurfaceFactory(new EGLWindowSurfaceFactory() {
                @Override
                public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display, EGLConfig config, Object nativeWindow) {
                    return egl.eglCreateWindowSurface(display, config, surface, null);
                }

                @Override
                public void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface) {
                    egl.eglDestroySurface(display, surface);                }
            });

            setEGLContextClientVersion(2);
            setRenderer(TextureController.this);
            setRenderMode(RENDERMODE_WHEN_DIRTY);
            //Control whether the EGL context is preserved when the
            // GLSurfaceView is paused and resumed.
            setPreserveEGLContextOnPause(true);
        }

        public void attachedToWindow() {
            super.onAttachedToWindow();
        }

        public void detachedFromWindow() {
            super.onDetachedFromWindow();
        }

        public void clear() {

        }
    }
}
