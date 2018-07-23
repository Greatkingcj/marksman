package com.huya.huyaijkplayer.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;

import com.huya.huyaijkplayer.R;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class SurfaceViewActivity extends AppCompatActivity {
    SurfaceView mSurfaceView;
    IjkMediaPlayer ijkMediaPlayer;
    long currentPosition = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        MediaController mediaController = new MediaController(this);
        ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mSurfaceView = findViewById(R.id.surface_view_player);
        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    ijkMediaPlayer.setDataSource("http://devimages.apple.com.edgekey.net/streaming/examples/bipbop_4x3/bipbop_4x3_variant.m3u8");
                    ijkMediaPlayer.prepareAsync();
                    ijkMediaPlayer.setDisplay(holder);
                    ijkMediaPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(IMediaPlayer iMediaPlayer) {
                            ijkMediaPlayer.start();
                            ijkMediaPlayer.seekTo(currentPosition);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (ijkMediaPlayer != null && ijkMediaPlayer.isPlaying()) {
                    currentPosition = ijkMediaPlayer.getCurrentPosition();
                    ijkMediaPlayer.stop();
                }
            }
        });
    }
}
