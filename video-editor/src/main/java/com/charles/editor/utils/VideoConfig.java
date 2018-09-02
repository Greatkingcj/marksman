package com.charles.editor.utils;

import android.content.Context;

import com.charles.editor.entry.VideoInfo;

/**
 * Created by yuejiaoli on 2017/10/16.
 * Demo层保存视频配置
 */
public class VideoConfig {

    private static final String TAG = "VideoConfig";
    private static VideoConfig sInstance;
    private final Context mContext;
    private VideoInfo mVideoInfo;

    public static VideoConfig getInstance(Context context) {
        if (sInstance == null)
            sInstance = new VideoConfig(context);
        return sInstance;
    }

    private VideoConfig(Context context) {
        mContext = context.getApplicationContext();
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        mVideoInfo = videoInfo;
    }

    public VideoInfo getVideoInfo() {
        return mVideoInfo;
    }
}
