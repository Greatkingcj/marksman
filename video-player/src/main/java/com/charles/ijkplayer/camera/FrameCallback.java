package com.charles.ijkplayer.camera;

/**
 * Created by charles on 2018/3/22.
 */

public interface FrameCallback {
    void onFrame(byte[] bytes, long time);
}
