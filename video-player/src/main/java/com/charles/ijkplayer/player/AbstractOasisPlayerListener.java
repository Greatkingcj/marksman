package com.charles.ijkplayer.player;

import android.support.annotation.NonNull;

/**
 * Extend the class if you want to implement only some of the methods of {@link OasisPlayerListener}
 * @author charles
 * @date 2018/8/21
 */

public abstract class AbstractOasisPlayerListener implements OasisPlayerListener {
    @Override public void onReady() { }
    @Override public void onStateChange(@PlayerConstants.PlayerState.State int state) { }
    @Override public void onPlaybackQualityChange(@NonNull @PlayerConstants.PlaybackQuality.Quality String playbackQuality) { }
    @Override public void onPlaybackRateChange(@NonNull @PlayerConstants.PlaybackRate.Rate String rate) { }
    @Override public void onError(@PlayerConstants.PlayerError.Error int error) { }
    @Override public void onApiChange() { }
    @Override public void onCurrentSecond(float second) { }
    @Override public void onVideoDuration(float duration) { }
    @Override public void onVideoLoadedFraction(float fraction) { }
    @Override public void onVideoId(@NonNull String videoId) { }
    @Override public void onVideoPath(@NonNull String videoPath) { }
}
