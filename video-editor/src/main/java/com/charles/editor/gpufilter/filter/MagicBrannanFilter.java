package com.charles.editor.gpufilter.filter;

import android.opengl.GLES20;

import com.charles.base.BaseApp;
import com.charles.editor.R;
import com.charles.editor.gpufilter.basefilter.GPUImageFilter;
import com.charles.editor.gpufilter.utils.OpenGlUtils;


public class MagicBrannanFilter extends GPUImageFilter {
	private int[] inputTextureHandles = {-1,-1,-1,-1,-1};
	private int[] inputTextureUniformLocations = {-1,-1,-1,-1,-1};
    private int mGLStrengthLocation;

	public MagicBrannanFilter(){
		super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.brannan));
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
        GLES20.glDeleteTextures(inputTextureHandles.length, inputTextureHandles, 0);
        for(int i = 0; i < inputTextureHandles.length; i++)
        	inputTextureHandles[i] = -1;
    }
	
	@Override
    protected void onDrawArraysAfter(){
		for(int i = 0; i < inputTextureHandles.length
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3));
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		}
	}
	  
	@Override
    protected void onDrawArraysPre(){
		for(int i = 0; i < inputTextureHandles.length 
				&& inputTextureHandles[i] != OpenGlUtils.NO_TEXTURE; i++){
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + (i+3) );
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, inputTextureHandles[i]);
			GLES20.glUniform1i(inputTextureUniformLocations[i], (i+3));
		}
	}
	
	@Override
    protected void onInit(){
		super.onInit();
		for(int i=0; i < inputTextureUniformLocations.length; i++)
			inputTextureUniformLocations[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture"+(2+i));
		mGLStrengthLocation = GLES20.glGetUniformLocation(mGLProgId,
				"strength");
	}
	
	@Override
    protected void onInitialized(){
		super.onInitialized();
		setFloat(mGLStrengthLocation, 1.0f);
	    runOnDraw(new Runnable(){
		    @Override
            public void run(){
		    	inputTextureHandles[0] = OpenGlUtils.loadTexture(BaseApp.application(), "filter/brannan_process.png");
				inputTextureHandles[1] = OpenGlUtils.loadTexture(BaseApp.application(), "filter/brannan_blowout.png");
				inputTextureHandles[2] = OpenGlUtils.loadTexture(BaseApp.application(), "filter/brannan_contrast.png");
				inputTextureHandles[3] = OpenGlUtils.loadTexture(BaseApp.application(), "filter/brannan_luma.png");
				inputTextureHandles[4] = OpenGlUtils.loadTexture(BaseApp.application(), "filter/brannan_screen.png");
		    }
	    });
	}
}
