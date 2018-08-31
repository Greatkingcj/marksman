package com.charles.ijkplayer.player;

/**
 * Created by charles on 2018/8/21.
 */

public interface Player {
    /**
     * Loads and automatically plays the specified video
     * @param videoId id of the video
     * @param startSeconds the time from which the video should start playing
     */
    void loadVideo(final String videoId, final float startSeconds);


    /**
     * Sets video path
     * @param path the path of the video
     */
    void setVideoPath(final String path);

    void play();

    void pause();

    /**
     * @param volumePercent Integer between 0 and 100
     */
    void setVolume(final int volumePercent);

    /**
     * @param time The absolute time in seconds to seek to
     */
    void seekTo(final float time);

    boolean addListener(OasisPlayerListener listener);
    boolean removeListener(OasisPlayerListener listener);
}
