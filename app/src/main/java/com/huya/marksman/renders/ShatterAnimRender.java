package com.huya.marksman.renders;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.huya.marksman.R;
import com.huya.marksman.listener.ShatterAnimListener;
import com.huya.marksman.programs.ShatterProgram;
import com.huya.marksman.util.openglutil.ShaderHelper;
import com.huya.marksman.util.openglutil.TextResourceReader;
import com.huya.marksman.util.openglutil.TextureHelper;
import com.huya.marksman.util.openglutil.VaryTools;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_CW;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glFrontFace;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glViewport;

/**
 *
 * @author charles
 * @date 2018/7/19
 */

public class ShatterAnimRender implements GLSurfaceView.Renderer{

    private static final String TAG = "ShtterAnimRender";

    private Context context;

    private GLSurfaceView glSurfaceView;
    private int fragsInX;
    private int fragsInY;
    private float aspectRatio;

    private ValueAnimator valueAnimator;
    private float animFraction;

    private ShatterProgram shatterProgram;
    //private int shatterProgram;
    private int shatterTexture;

    private ShatterItemRender shatterItemRender;

    private VaryTools varyTools;

    public ShatterAnimRender(GLSurfaceView glSurfaceView, Context context) {
        this.glSurfaceView = glSurfaceView;
        this.context = context;
        this.varyTools = new VaryTools();
    }

    private ShatterAnimListener shatterAnimListener;
    public void setShatterAnimListener(ShatterAnimListener shatterAnimListener) {
        this.shatterAnimListener = shatterAnimListener;
    }

    public void startAnimation(Bitmap bitmap) {
        /**
         * 主线程执行
         */
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            valueAnimator = ValueAnimator.ofFloat(0f, 2f);
            valueAnimator.setDuration(1500);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(animation -> {
                animFraction = (float) animation.getAnimatedValue();
                glSurfaceView.requestRender();
            });

            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (shatterAnimListener != null) {
                        shatterAnimListener.onAnimFinish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            valueAnimator.start();
            if (shatterAnimListener != null) {
                shatterAnimListener.onAnimStart();
            }
        });

        glSurfaceView.queueEvent(() -> {
            shatterTexture = TextureHelper.loadTexture(bitmap);
            glSurfaceView.requestRender();
        });
    }

    public void stopAnimation() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    public void destroy() {
        glSurfaceView = null;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glFrontFace(GL_CW);

        shatterProgram = new ShatterProgram(context);
       /* shatterProgram = ShaderHelper.buildProgram(
                TextResourceReader
                        .readTextFileFromResource(context, R.raw.shatter_vertex_shader),
                TextResourceReader
                        .readTextFileFromResource(context, R.raw.shatter_fragment_shader));*/

       Log.e(TAG, "shatterProgram" + shatterProgram.getProgram());
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        aspectRatio = (float) width / height;
        Log.e(TAG, "width: " + width + "height: " + height );
        Log.e(TAG, "aspectRatio" + aspectRatio);
        varyTools.setCamera(0f, 0f, 0f, 0f, 0f, 10f, 0f, 1f, 0f);
        varyTools.frustum(-aspectRatio, aspectRatio, -1.0f, 1.0f, 1.0f, 10f);
        fragsInX = 20;
        fragsInY = (int) (fragsInX / aspectRatio);

        /* GL线程初始化碎片数据 */
        float[] vertData = initPositionData();
        float[] textureData = initTextureData();
        shatterItemRender = new ShatterItemRender(shatterProgram.getProgram(), vertData, textureData);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (shatterProgram.getProgram() <= 0) {
            Log.e(TAG, "Program is not load.");
            return ;
        }
        if (shatterTexture <= 0) {
            Log.e(TAG, "Texture is not load.");
            return ;
        }
        if (shatterItemRender == null) {
            Log.e(TAG, "Frag item render is not init");
            return ;
        }

        varyTools.pushMatrix();
        varyTools.translate(0, 0, 1.0001f);

        shatterProgram.useProgram();
        shatterProgram.setUniforms(varyTools.getFinalMatrix(), shatterTexture, animFraction);

        shatterItemRender.onRefreshRender();
        glBindTexture(GL_TEXTURE_2D, 0);
        varyTools.popMatrix();

        /*if (shatterProgram <= 0) {
            Log.d(TAG, "Program is not load.");
            return ;
        }
        if (shatterTexture <= 0) {
            Log.d(TAG, "Texture is not load.");
            return ;
        }
        if (shatterItemRender == null) {
            Log.d(TAG, "Frag item render is not init");
            return ;
        }

        *//* 加载Program到OpenGL环境中 *//*
        glUseProgram(shatterProgram);
        Log.d(TAG, "load program");

        varyTools.pushMatrix();

        *//* 加载动画位移偏移值 *//*
        int moveDistanceHandle = GLES20.glGetUniformLocation(shatterProgram, "u_AnimationFraction");
        GLES20.glUniform1f(moveDistanceHandle, animFraction);

        *//* 加载变换矩阵 *//*
        int mvpMatrixHandle = GLES20.glGetUniformLocation(shatterProgram, "u_MVPMatrix");
        varyTools.translate(0, 0, 1.0001f);
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, varyTools.getFinalMatrix(), 0);

        *//* 加载纹理 *//*
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shatterTexture);
        int textureHandle = GLES20.glGetUniformLocation(shatterProgram, "u_TextureUnit");
        GLES20.glUniform1i(textureHandle, 0);
        *//* 绘制碎片纹理 *//*
        shatterItemRender.onRefreshRender();
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        varyTools.popMatrix();*/
    }

    /**
     * 初始化顶点坐标数据
     */
    private float[] initPositionData() {
        /* 每个碎片都是一个正方形, 由6个顶点决定, 每个顶点由x, y, z三个方向决定 */
        float[] positionData = new float[6 * 3 * fragsInX * fragsInY];

        float height = 1f;
        float width = height * aspectRatio;

        final float stepX = width * 2f / fragsInX;
        final float stepY = height * 2f / fragsInY;


        final float minPositionX = -width;
        final float minPositionY = -height;

        int positionDataOffset = 0;
        for (int x = 0; x < fragsInX; x++) {
            for (int y = 0; y < fragsInY; y++) {

                float z = (float) Math.random();

                final float x1 = minPositionX + x * stepX;
                final float x2 = x1 + stepX;

                final float y1 = minPositionY + y * stepY;
                final float y2 = y1 + stepY;

                // Define points for a plane.
                final float[] p1 = {x1, y2, z};
                final float[] p2 = {x2, y2, z};
                final float[] p3 = {x1, y1, z};
                final float[] p4 = {x2, y1, z};

                int elementsPerPoint = p1.length;
                final int size = elementsPerPoint * 6;
                final float[] thisPositionData = new float[size];

                int offset = 0;
                // Build the triangles
                //  1---2
                //  | / |
                //  3---4
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p1[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p3[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p2[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p3[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p4[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    thisPositionData[offset++] = p2[i];
                }

                System.arraycopy(
                        thisPositionData, 0,
                        positionData, positionDataOffset,
                        thisPositionData.length
                );
                positionDataOffset += thisPositionData.length;
            }
        }

        return positionData;
    }

    /**
     * 初始化纹理坐标数据
     */
    private float[] initTextureData() {
        float[] textureData = new float[6 * 2 * fragsInX * fragsInY];

        final float stepX = 1f / fragsInX;
        final float stepY = 1f / fragsInY;

        int textureDataOffset = 0;
        for (int x = fragsInX - 1; x >= 0; x--) {
            for (int y = fragsInY - 1; y >= 0; y--) {
                final float u0 = x * stepX;
                final float v0 = y * stepY;
                final float u1 = u0 + stepX;
                final float v1 = v0 + stepY;

                final int elementsPerPoint = 2;
                final int size = elementsPerPoint * 6;
                final float[] itemFrag = new float[size];

                int offset = 0;
                // Build the triangles
                //  1---2
                //  | / |
                //  3---4
                // Define points for a plane.

                final float[] p1 = {u1, v0};
                final float[] p2 = {u0, v0};
                final float[] p3 = {u1, v1};
                final float[] p4 = {u0, v1};

                for (int i = 0; i < elementsPerPoint; i++) {
                    itemFrag[offset++] = p1[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    itemFrag[offset++] = p3[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    itemFrag[offset++] = p2[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    itemFrag[offset++] = p3[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    itemFrag[offset++] = p4[i];
                }
                for (int i = 0; i < elementsPerPoint; i++) {
                    itemFrag[offset++] = p2[i];
                }

                System.arraycopy(
                        itemFrag, 0,
                        textureData, textureDataOffset,
                        itemFrag.length
                );
                textureDataOffset += itemFrag.length;
            }
        }

        return textureData;
    }
}
