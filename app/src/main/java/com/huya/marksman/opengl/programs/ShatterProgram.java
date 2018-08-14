package com.huya.marksman.opengl.programs;

import android.content.Context;

import com.huya.marksman.R;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by charles on 2018/7/19.
 */

public class ShatterProgram extends ShaderProgram{
    private final int u_MVPMatrixLocation;
    private final int u_AnimationFractionLocation;
    private final int u_TextureUnitLocatin;

    //private final int a_PositonLocation;
    //private final int a_TextureCoordinatesLocation;
    public ShatterProgram(Context context) {
        super(context, R.raw.shatter_vertex_shader,
                R.raw.shatter_fragment_shader);
        u_MVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);
        u_AnimationFractionLocation = glGetUniformLocation(program, U_ANIMATION_FRACTION);
        u_TextureUnitLocatin = glGetUniformLocation(program, U_TEXTURE_UNIT);

        //a_PositonLocation = glGetAttribLocation(program, A_POSITION);
        //a_TextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId, float mAnimFraction) {
        glUniformMatrix4fv(u_MVPMatrixLocation, 1, false, matrix, 0);

        glUniform1f(u_AnimationFractionLocation, mAnimFraction);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId);

        glUniform1i(u_TextureUnitLocatin, 0);
    }

    /*public int getPositionAttributeLocation() {
        return a_PositonLocation;
    }*/

    /*public int getTextureCoordinateLocation() {
        return a_TextureCoordinatesLocation;
    }*/

    public int getProgram() {
        return program;
    }
}
