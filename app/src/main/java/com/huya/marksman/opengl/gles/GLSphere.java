package com.huya.marksman.opengl.gles;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by chenyuehai on 2017/8/7.
 */

public class GLSphere {

    private FloatBuffer verticesBuffer;
    private FloatBuffer textureCoordinateBuffer;
    private ShortBuffer indexBuffer;
    private int indicesCount;

    /**
     *
     * @param radius
     * @param rings
     * @param sectors
     */
    public GLSphere(float radius, int rings, int sectors) {
        int numPoint = (rings + 1) * (sectors + 1);
        float[] vertexData = new float[numPoint * 3];
        float[] textureCoordinateData = new float[numPoint * 2];
        short[] indices = new short[numPoint * 6];


        int numParallels = sectors / 2;
        float angleStep = (float) ((2.0f * Math.PI) / ((float) sectors));

        for (int i = 0; i < numParallels + 1; i++) {
            for (int j = 0; j < sectors + 1; j++) {
                int vertex = (i * (sectors + 1) + j) * 3;
                vertexData[vertex + 0] = (float) (radius * Math.sin(angleStep * (float) i) * Math.sin(angleStep * (float) j));
                vertexData[vertex + 1] = (float) (radius * Math.cos(angleStep * (float) i));
                vertexData[vertex + 2] = (float) (radius * Math.sin(angleStep * (float) i) * Math.cos(angleStep * (float) j));

                int texIndex = (i * (sectors + 1) + j) * 2;
                textureCoordinateData[texIndex + 0] = (float) j / (float) sectors;
                textureCoordinateData[texIndex + 1] = 1.0f - ((float) i / (float) (numParallels));
            }
        }

        int index = 0;
        for (int i = 0; i < numParallels; i++) {
            for (int j = 0; j < sectors; j++) {
                //这个地方是一个球体绘制了正反两个面
                indices[index++] = (short) (i * (sectors + 1) + j);
                indices[index++] = (short) ((short) (i + 1) * (sectors + 1) + j);
                indices[index++] = (short) ((short) (i + 1) * (sectors + 1) + (j + 1));
                indices[index++] = (short) (i * (sectors + 1) + j);
                indices[index++] = (short) ((short) (i + 1) * (sectors + 1) + (j + 1));
                indices[index++] = (short) ((short) i * (sectors + 1) + (j + 1));
            }
        }

        verticesBuffer = ByteBuffer.allocateDirect(vertexData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        verticesBuffer.put(vertexData);
        verticesBuffer.position(0);

        textureCoordinateBuffer = ByteBuffer.allocateDirect(textureCoordinateData.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        textureCoordinateBuffer.put(textureCoordinateData);
        textureCoordinateBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(indices.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.position(0);

        indicesCount = indices.length;
    }


    public void bindVerticesBuffer(int positionHandle) {
        verticesBuffer.position(0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, verticesBuffer);
        GLError.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLError.checkGlError("glEnableVertexAttribArray");
    }

    public void bindTextureCoordinateBuffer(int textureCoordinateHandle) {
        textureCoordinateBuffer.position(0);

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordinateBuffer);
        GLError.checkGlError("glVertexAttribPointer");
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        GLError.checkGlError("glEnableVertexAttribArray");
    }


    public void draw() {
        indexBuffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indicesCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }
}