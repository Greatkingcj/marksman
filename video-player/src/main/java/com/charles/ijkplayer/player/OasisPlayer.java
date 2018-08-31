package com.charles.ijkplayer.player;

import android.content.Context;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by charles on 2018/8/21.
 */

public class OasisPlayer implements Player{

    private final Set<OasisPlayerListener> oasisPlayerListeners;

    public OasisPlayer(Context context) {
        oasisPlayerListeners = new HashSet<>();
    }

    @Override
    public void loadVideo(String videoId, float startSeconds) {

    }

    @Override
    public void setVideoPath(String path) {

    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void setVolume(int volumePercent) {

    }

    @Override
    public void seekTo(float time) {

    }

    public Collection<OasisPlayerListener> getListeners() {
        return Collections.unmodifiableCollection(new HashSet<>(oasisPlayerListeners));
    }

    @Override
    public boolean addListener(OasisPlayerListener listener) {
        return oasisPlayerListeners.add(listener);
    }

    @Override
    public boolean removeListener(OasisPlayerListener listener) {
        return oasisPlayerListeners.remove(listener);
    }
}
