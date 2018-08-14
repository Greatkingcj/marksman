package com.huya.marksman.opengl.gles;

import android.opengl.GLES20;

/**
 * Created by chenyuehai on 2017/8/7.
 */

public class GLError {
    private static final String TAG = "GLError";

    public static void checkGlError(String label) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            StringBuilder sb = new StringBuilder();
            sb.append(label).append(" errorCode:").append(error);
        }
    }
}
