package com.huya.huyaijkplayer.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huya.huyaijkplayer.R;
import com.huya.huyaijkplayer.controller.HuyaPlayerController;
import com.huya.huyaijkplayer.view.videoview.HuyaVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class HuyaVideoViewActivity extends AppCompatActivity {
    private String mVideoPath = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
    private HuyaVideoView mVideoView;
    private HuyaPlayerController mController;
    private boolean mBackPressed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huya_video_view);

        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.loadLibrariesOnce(null);
        mController = new HuyaPlayerController(this);
        mVideoView = findViewById(R.id.huya_video_view);
        mVideoView.setController(mController);

        mVideoView.setVideoPath(mVideoPath);
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        if (mVideoView.isNormal()) {

        } else if (mVideoView.isFullScreen()) {
            mVideoView.exitFullScreen();
        }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        IjkMediaPlayer.native_profileEnd();
    }
}
