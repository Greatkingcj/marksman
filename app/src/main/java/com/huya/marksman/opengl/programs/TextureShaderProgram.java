package com.huya.marksman.opengl.programs;

import android.content.Context;

import com.huya.marksman.R;

import static android.opengl.GLES10.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by charles on 2018/6/30.
 */

public class TextureShaderProgram extends ShaderProgram{

    private final int uMatrixLoaction;
    private final int uTextureUnitLocation;
    private final int uTextureBgLocation;

    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public TextureShaderProgram(Context context) {
        super(context, R.raw.texture_vertex_shader,
                R.raw.texture_fragment_shader);

        uMatrixLoaction = glGetUniformLocation(program, U_MATRIX);
        uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT);
        uTextureBgLocation = glGetUniformLocation(program, U_BACKGROUND);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms(float[] matrix, int textureId, int textureBg) {
        glUniformMatrix4fv(uMatrixLoaction, 1, false, matrix, 0);

        glActiveTexture(GL_TEXTURE0);

        glBindTexture(GL_TEXTURE_2D, textureId);

        glUniform1i(uTextureUnitLocation, 0);

        glActiveTexture(GL_TEXTURE1);

        glBindTexture(GL_TEXTURE_2D, textureBg);

        glUniform1i(uTextureUnitLocation, 1);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
}
