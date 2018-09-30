package com.charles.editor.preview;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Surface;

import com.charles.editor.entry.VideoInfo;
import com.charles.editor.gpufilter.SlideGpuFilterGroup;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by charles on 2018/9/30.
 */

public class VideoPreviewView extends GLSurfaceView implements GLSurfaceView.Renderer, MediaPlayerWrapper.IMediaCallback{

    VideoDrawer mDrawer;
    MediaPlayerWrapper mMediaPlayer;

    private MediaPlayerWrapper.IMediaCallback callback;

    public VideoPreviewView(Context context) {
        super(context);
        init(context);
    }

    public VideoPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        setPreserveEGLContextOnPause(false);
        setCameraDistance(100);
        mDrawer = new VideoDrawer(context, context.getResources());

        mMediaPlayer = new MediaPlayerWrapper();
        mMediaPlayer.setOnCompletionListener(this);
    }

    public void setVideoPath(List<String> paths) {
        mMediaPlayer.setDataSource(paths);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mDrawer.onSurfaceCreated(gl, config);
        SurfaceTexture surfaceTexture = mDrawer.getSurfaceTexture();
        surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });

        Surface surface = new Surface(surfaceTexture);
        mMediaPlayer.setSurface(surface);

        try {
            mMediaPlayer.prepare();
        } catch (IOException e) {

        }
        mMediaPlayer.start();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mDrawer.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mDrawer.onDrawFrame(gl);
    }

    public void onDestroy() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
    }

    public void onTouch(final MotionEvent event) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDrawer.onTouch(event);
            }
        });
    }

    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener) {
        mDrawer.setOnFilterChangeListener(listener);
    }

    @Override
    public void onVideoPrepare() {
        if (callback != null) {
            callback.onVideoPrepare();
        }
    }

    @Override
    public void onVideoStart() {
        if (callback != null) {
            callback.onVideoStart();
        }
    }

    @Override
    public void onVideoPause() {
        if (callback != null) {
            callback.onVideoPause();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (callback != null){
            callback.onCompletion(mp);
        }
    }

    @Override
    public void onVideoChanged(final VideoInfo info) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mDrawer.onVideoChanged(info);
            }
        });
        if (callback!=null) {
            callback.onVideoChanged(info);
        }
    }

    /**
     * isPlaying now
     * */
    public boolean isPlaying(){
        return mMediaPlayer.isPlaying();
    }
    /**
     * pause play
     * */
    public void pause(){
        mMediaPlayer.pause();
    }
    /**
     * start play video
     * */
    public void start(){
        mMediaPlayer.start();
    }
    /**
     * 跳转到指定的时间点，只能跳到关键帧
     * */
    public void seekTo(int time){
        mMediaPlayer.seekTo(time);
    }
    /**
     * 获取当前视频的长度
     * */
    public int getVideoDuration(){
        return mMediaPlayer.getCurVideoDuration();
    }

    /**
     * 切换美颜状态
     * */
    public void switchBeauty(){
        mDrawer.switchBeauty();
    }


    public void setIMediaCallback(MediaPlayerWrapper.IMediaCallback callback){
        this.callback = callback;
    }
}
