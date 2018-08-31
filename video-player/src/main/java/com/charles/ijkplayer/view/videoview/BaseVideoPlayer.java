package com.charles.ijkplayer.view.videoview;

/**
 * Created by charles on 2018/3/12.
 */

import java.util.Map;

/**
 * videoview的抽象接口，videoview给外界调用播放相关接口
 */
public interface BaseVideoPlayer {

    /**
     * 设置视频Url，以及请求headers
     * @param url
     * @param headers
     */
    void setUp(String url, Map<String, String> headers);

    /**
     * start play
     */
    void start();

    /**
     * play at position
     * @param position
     */
    void start(long position);

    void restart();

    void pause();

    void seekTo(long pos);

    void setVolume(int volume);

    /**
     * ijkplayer才可以设置，其它不可以
     * @param speed
     */
    void setSpeed(float speed);

    void continueFromLastPosition(boolean continueFromLastPosition);

    /**
     * 播放器当前状态
     * Idle, Preparing, Prepared, Playing, BufferingPlaying, BufferingPaused, Paused, Error, Completed
     */
    boolean isIdle();
    boolean isPreparing();
    boolean isPrepared();
    boolean isPlaying();
    boolean isBufferingPlaying();
    boolean isBufferingPaused();
    boolean isPaused();
    boolean isError();
    boolean isCompleted();

    /**
     * 播放器窗口状态 （全屏，小窗，正常）
     */
    boolean isFullScreen();
    boolean isTinyWindow();
    boolean isNormal();

    int getMaxVolume();

    int getVolume();

    long getDuration();

    long getCurrentPosition();

    /**
     * 获取视频缓冲百分比
     *
     * @return 缓冲百分比
     */
    int getBufferPercentage();

    float getSpeed(float speed);

    /**
     * 获取网络加载速度
     *
     * @return 网络加载速度
     */
    long getTcpSpeed();

    void enterFullScreen();

    boolean exitFullScreen();

    void enterTinyWindow();

    boolean exitTinyWindow();

    /**
     * 此处只释放播放器（如果要释放播放器并恢复控制器状态需要调用{@link #release()}方法）
     * 不管是全屏、小窗口还是Normal状态下控制器的UI都不恢复初始状态
     * 这样以便在当前播放器状态下可以方便的切换不同的清晰度的视频地址
     */
    void releasePlayer();

    /**
     * 释放INiceVideoPlayer，释放后，内部的播放器被释放掉，同时如果在全屏、小窗口模式下都会退出
     * 并且控制器的UI也应该恢复到最初始的状态.
     */
    void release();
}
