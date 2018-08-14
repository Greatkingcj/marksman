package com.huya.marksman.opengl.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.huya.marksman.R;

/**
 * Created by charles on 2018/8/14.
 */

public class PanoProgram extends ShaderProgram{

    private static final String U_MVP_MATRIX = "u_MVPMatrix";
    private static final String U_TEXTURE_UNIT = "u_TextureUnit";
    private static final String A_POSITION = "a_Position";
    private static final String A_TEX_COORDINATE = "a_TexCoordinate";
    /**
     * uniform location
     */
    private final int uMVPMatrixLocation;
    private final int uTextureUnitLocation;
    /**
     *Attribute locations
     */
    private final int aPositionLocation;
    private final int aTexCoordinateLocation;


    public PanoProgram(Context context) {
        super(context, R.raw.pano_vertex_shader, R.raw.pano_fragment_shader);
        uMVPMatrixLocation = GLES20.glGetUniformLocation(program, U_MVP_MATRIX);
        uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);
        aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
        aTexCoordinateLocation = GLES20.glGetAttribLocation(program, A_TEX_COORDINATE);
    }

    public int getMVPMatrixLocation() {
        return uMVPMatrixLocation;
    }

    public int getTextureUnitLocation() {
        return uTextureUnitLocation;
    }

    public int getPositionLocation() {
        return aPositionLocation;
    }

    public int getTexCoordinateLocation() {
        return aTexCoordinateLocation;
    }
}
