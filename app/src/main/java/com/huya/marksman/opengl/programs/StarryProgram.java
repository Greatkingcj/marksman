package com.huya.marksman.opengl.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.huya.marksman.R;

public class StarryProgram extends ShaderProgram{
    public StarryProgram(Context context) {
        super(context, R.raw.star_vertex_shader,
                R.raw.star_fragment_shader);
        GLES20.glBindAttribLocation(program, 0, "a_Position");
        GLES20.glBindAttribLocation(program, 1, "a_TexCoordinate");
        GLES20.glBindAttribLocation(program, 1, "a_TileXY");
    }

    public int getProgram() {
        return  program;
    }
}
