package com.huya.marksman.opengl.renders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.huya.marksman.MarkApplication;
import com.huya.marksman.opengl.gles.GLSphere;
import com.huya.marksman.opengl.gles.GLSphereProgram;
import com.huya.marksman.opengl.gles.GLTexture;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by charles on 2018/8/12.
 */

public class PanoRenderer implements GLSurfaceView.Renderer{
    private GLSphere sphere;
    private GLSphereProgram sphereProgram;
    private boolean textureRequestUpdate = true;
    private int textureId = 0;

    private float[] rotationMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] modelViewMatrix = new float[16];
    private float[] modelViewProjectionMatrix = new float[16];
    private float[] firstRotationMatrix = new float[16];
    private float[] lastRotationMatrix = new float[16];
    private float[] angleChanged = new float[3];
    private float ratio;
    private float sensorRotationX = 0f;
    private float sensorRotationY = 0f;
    private float sensorRotationZ = 0f;
    private float dragRotationX = 0f;
    private float dragRotationY = 0f;
    private float initRotationX = 0f;
    private float initRotationY = 0f;
    private Bitmap textureBitmap = null;
    private float mChangeRotateX;
    private float mChangeRotateY;

    public PanoRenderer() {
        initSphereAndMatrix();
    }

    public int getRotationX() {
        int rotation = (int) (initRotationX + sensorRotationX + dragRotationX);
        return rotation % 360;
    }

    public int getRotationY() {
        int rotation = (int) (initRotationY + sensorRotationY + dragRotationY);
        rotation = Math.min(rotation, 90);
        rotation = Math.max(rotation, -90);

        return rotation % 360;
    }

    public void addDragRotationX(float x) {
        dragRotationX += x;
    }

    public void addDragRotationY(float y) {
        dragRotationY += y;
        dragRotationY = Math.min(dragRotationY, 90 - sensorRotationY);
        dragRotationY = Math.max(dragRotationY, -90 - sensorRotationY);
    }

    public void setInitRotation(int rotationX, int rotationY) {
        initRotationX = 0;
        initRotationY = 0;
    }

    public void setRotationVector(float[] vector, boolean isFirst) {
        if (isFirst) {
            SensorManager.getRotationMatrixFromVector(firstRotationMatrix, vector);
            sensorRotationX = 0f;
            sensorRotationY = 0f;
            sensorRotationZ = 0f;
            dragRotationX = 0f;
            dragRotationY = 0f;
            System.arraycopy(firstRotationMatrix,0,lastRotationMatrix,0,firstRotationMatrix.length);
        }
        else {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, vector);
            SensorManager.getAngleChange(angleChanged, rotationMatrix, lastRotationMatrix);
            mChangeRotateX = (float) Math.toDegrees(angleChanged[2]);
            mChangeRotateY = (float) Math.toDegrees(angleChanged[1]);
            sensorRotationX = sensorRotationX + mChangeRotateX * 0.85f;
            sensorRotationY = sensorRotationY + mChangeRotateY * 0.85f;
            sensorRotationY = Math.min(sensorRotationY, 90 - dragRotationY);
            sensorRotationY = Math.max(sensorRotationY, -90 - dragRotationY);
            sensorRotationZ = 0f;
            System.arraycopy(rotationMatrix,0,lastRotationMatrix,0,rotationMatrix.length);
        }
    }

    private void initSphereAndMatrix() {
        sphere = new GLSphere(18, 75, 150);
        sphereProgram = new GLSphereProgram();

        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.setIdentityM(projectionMatrix, 0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, -1.0f, 0.0f);
        loadTextureBitmap();
    }

    private void loadTextureBitmap() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        try {
            textureBitmap = BitmapFactory.decodeStream(MarkApplication.getApplication().getAssets().open("test3.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTexture() {
        if (textureRequestUpdate && (textureBitmap != null)) {
            textureRequestUpdate = false;
            textureId = GLTexture.loadTexture(textureBitmap);
            textureBitmap = null;
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        sphereProgram.init();
        loadTextureBitmap();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        ratio = (float) width / height;
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        loadTexture();

        if (textureId <= 0) {
            return;
        }

        sphereProgram.use();
        sphere.bindVerticesBuffer(sphereProgram.getPositionHandle());
        sphere.bindTextureCoordinateBuffer(sphereProgram.getTextureCoordinateHandle());

        float rotationY = initRotationY + sensorRotationY + dragRotationY;
        rotationY = Math.min(rotationY, 90);
        rotationY = Math.max(rotationY, -90);

        Matrix.perspectiveM(projectionMatrix, 0, 90, ratio, 1f, 500f);
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.rotateM(modelMatrix, 0, rotationY, 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, initRotationX + sensorRotationX + dragRotationX - sensorRotationZ, 0.0f, 1.0f, 0.0f);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
        GLES20.glUniformMatrix4fv(sphereProgram.getMVPHandle(), 1, false, modelViewProjectionMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(sphereProgram.getTextureHandle(), 0);

        sphere.draw();
    }
}
