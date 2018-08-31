package com.charles.ijkplayer.proxy;

/**
 * Created by charles on 2018/3/14.
 */

import com.charles.ijkplayer.view.videoview.BaseVideoPlayer;

/**
 * 统一管理应用中的所有videoview，
 * 例如对于一个RecyclerView中的所有item中的huyavideoview做控制
 * 采用单例实现
 */
public class VideoPlayerManager {

    private BaseVideoPlayer mVideoPlayer;
    private static VideoPlayerManager sInstance;
    private VideoPlayerManager() {

    }

    /**
     * 采用DCL单例，保证同一时刻只有一个视频在播放
     * @return
     */
    public static VideoPlayerManager getInstance() {
        if (sInstance == null) {
            synchronized (VideoPlayerManager.class) {
                if (sInstance == null) {
                    sInstance = new VideoPlayerManager();
                }
            }
        }
        return sInstance;
    }

    public void setCurrentVideoPlayer(BaseVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    public BaseVideoPlayer getCurrentVideoPlayer() {
        return mVideoPlayer;
    }

    public void releaseVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressed() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
