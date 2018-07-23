package com.huya.huyaijkplayer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.huya.huyaijkplayer.R;
import com.huya.huyaijkplayer.controller.AndroidMediaController;
import com.huya.huyaijkplayer.manager.AndroidVideoCacheManager;
import com.huya.huyaijkplayer.view.videoview.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class VideoActivity extends AppCompatActivity {
    private String mVideoPath;

    private IjkVideoView mVideoView;
    private AndroidMediaController mMediaController;

    private boolean mBackPressed;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        mVideoPath = getIntent().getStringExtra("videoPath");
        mMediaController = new AndroidMediaController(this, false);

        //init player
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        ijkMediaPlayer.setOption(1, "analyzemaxduration", 100L);
        ijkMediaPlayer.setOption(1, "probesize", 10240L);
        ijkMediaPlayer.setOption(1, "flush_packets", 1L);
        ijkMediaPlayer.setOption(4, "packet-buffering", 0L);
        ijkMediaPlayer.setOption(4, "framedrop", 1L);

        mVideoView = findViewById(R.id.video_view);
        mVideoView.setMediaController(mMediaController);
        String proxyUrl = AndroidVideoCacheManager.getProxy(this).getProxyUrl(mVideoPath);
        mVideoView.setVideoPath(proxyUrl);
        //mVideoView.setVideoPath(mVideoPath);
        //mVideoView.
        //mVideoView.start();
    }

    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();
        } else {
            mVideoView.enterBackground();
        }
        IjkMediaPlayer.native_profileEnd();
    }
}
