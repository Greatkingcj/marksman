package com.charles.ijkplayer.player;

/**
 * Created by charles on 2018/8/21.
 */

public interface OasisPlayerListener {

    /**
     * Called when the player is ready to play videos. You should start interacting with the player only after it is ready.
     */
    void onReady();

    /**
     * Use this method to track the state of the playback. Check {@link PlayerConstants.PlayerState.State}
     * to see all the possible states.
     * @param state a state from {@link PlayerConstants.PlayerState.State}
     */
    void onStateChange(@PlayerConstants.PlayerState.State int state);

    /**
     * Use this method to be notified when the quality of the playback changes
     * @param playbackQuality
     */
    void onPlaybackQualityChange(@PlayerConstants.PlaybackQuality.Quality String playbackQuality);

    /**
     * Use this method to be notified when the speed of the playback changes
     * @param playbackRate
     */
    void onPlaybackRateChange(@PlayerConstants.PlaybackRate.Rate String playbackRate);

    /**
     * Use this method to be notified when an error occurs in the player
     * @param error
     */
    void onError(@PlayerConstants.PlayerError.Error int error);

    void onApiChange();

    /**
     * Use this method to know at which second of the video duration the currently playing video is
     * @param second current second of the playback
     */
    void onCurrentSecond(float second);

    /**
     * Use this method to know the duration in seconds of the currently playing video.
     * @param duration total duration of the video
     */
    void onVideoDuration(float duration);

    /**
     * Use this method to know the percentage of the video that the player shows as already buffered.
     * @param loadedFraction a number between 0 and 1 that specifies the percentage of the video that the player shows as buffered
     */
    void onVideoLoadedFraction(float loadedFraction);

    /**
     * @param videoId
     */
    void onVideoId(String videoId);

    /**
     * @param videoPath
     */
    void onVideoPath(String videoPath);
}
