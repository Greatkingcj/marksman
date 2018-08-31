package com.charles.ijkplayer.activitys;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huya.ijkplayer.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGlActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_gl);

        /*if (supportGlEs20(this)) {
            ActivityManager activityManager = (ActivityManager) this.getSystemService(
                    Context.ACTIVITY_SERVICE);
            int version = activityManager.getDeviceConfigurationInfo().reqGlEsVersion;
            Toast.makeText(this, "GLES 2.0 not Supported" + "GLES Version: " + version, Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/
        mGLSurfaceView = findViewById(R.id.gl_surface_view);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setEGLConfigChooser(8,8,8,8,16,0);
        mGLSurfaceView.setRenderer(new MyRender());
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    private static class MyRender implements GLSurfaceView.Renderer {

        private static final String VERTEX_SHADER =
                "attribute vec4 vPosition;\n"
                + "uniform mat4 uMVPMatrix;\n"
                + "void main() {\n"
                + " gl_Position = uMVPMatrix * vPosition;\n"
                + "}";
        private static final String FRAGMENT_SHADER =
                "precision mediump float;\n"
                + "void main() {\n"
                + " gl_FragColor = vec4(0.5, 0, 0, 1);\n"
                + "}";

        private static final float[] VERTEX = {   // in counterclockwise order:
                0, 1, 0,  // top
                -0.5f, -1, 0,  // bottom left
                1, -1, 0,  // bottom right
        };

        private final FloatBuffer mVertexBuffer;
        private final float[] mMVPMatrix = new float[16];

        private int mProgram;
        private int mPositionHandle;
        private int mMatrixHandle;

        MyRender() {
            mVertexBuffer = ByteBuffer.allocateDirect(VERTEX.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer()
                    .put(VERTEX);
            mVertexBuffer.position(0);
        }

        static int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

            mProgram = GLES20.glCreateProgram();
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);

            GLES20.glUseProgram(mProgram);

            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT,
                    false, 12, mVertexBuffer);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0,0, width, height);

            Matrix.perspectiveM(mMVPMatrix, 0, 45, (float) width / height, 0.1f, 100f);
            Matrix.translateM(mMVPMatrix, 0, 0f, 0f, -2.5f);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }
    }

    public static boolean supportGlEs20(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(
                Context.ACTIVITY_SERVICE);
        return activityManager.getDeviceConfigurationInfo().reqGlEsVersion >= 0x00020000;
    }
}
