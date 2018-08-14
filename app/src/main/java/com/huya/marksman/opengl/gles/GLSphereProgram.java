package com.huya.marksman.opengl.gles;

import android.opengl.GLES20;

/**
 * Created by chenyuehai on 2017/8/7.
 */

public class GLSphereProgram {

    private static final String VSH =
            "attribute vec3 inPosition;\n" +
                    "attribute vec2 inTextureCoordinate;\n" +
                    "varying vec2 outTextureCoordinate;\n" +
                    "uniform mat4 inMVP;\n" +
                    "void main() {\n" +
                    "    gl_Position = inMVP * vec4(inPosition, 1.0);\n" +
                    "    outTextureCoordinate = inTextureCoordinate;\n" +
                    "}";
    private static final String FSH =
            "precision mediump float;\n" +
                    "varying vec2 outTextureCoordinate;\n" +
                    "uniform sampler2D inTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(inTexture, outTextureCoordinate);\n" +
                    "}";

    private int programId;
    private int inPositionHandle;
    private int inMVPHandle;
    private int inTextureHandle;
    private int inTextureCoordinateHandle;

    public GLSphereProgram() {

    }

    public void init() {
        programId = GLProgram.createProgram(VSH, FSH);
        inPositionHandle = GLES20.glGetAttribLocation(programId, "inPosition");
        inMVPHandle = GLES20.glGetUniformLocation(programId, "inMVP");
        inTextureHandle = GLES20.glGetUniformLocation(programId, "inTexture");
        inTextureCoordinateHandle = GLES20.glGetAttribLocation(programId, "inTextureCoordinate");

    }

    public void use() {
        GLES20.glUseProgram(programId);
    }

    public int getTextureHandle() {
        return inTextureHandle;
    }

    public int getTextureCoordinateHandle() {
        return inTextureCoordinateHandle;
    }

    public int getPositionHandle() {
        return inPositionHandle;
    }

    public int getMVPHandle() {
        return inMVPHandle;
    }

}
