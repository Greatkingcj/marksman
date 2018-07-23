package com.huya.huyaijkplayer.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.huya.huyaijkplayer.R;
import com.huya.huyaijkplayer.manager.VideoPlayerManager;
import com.huya.huyaijkplayer.view.videoview.HuyaVideoView;

import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Video> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        for (int i = 0; i < 10 ; i ++) {
            Video video = new Video();
            video.setImageUrl("");
            video.setLength(10);
            video.setTitle("播放列表测试");
            video.setVideoUrl("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4");
            list.add(video);
        }

        VideoAdapter adapter = new VideoAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
            @Override
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                HuyaVideoView videoView = ((VideoAdapter.VideoViewHolder)holder).mVideoView;
                if (videoView == VideoPlayerManager.getInstance().getCurrentVideoPlayer()) {
                    VideoPlayerManager.getInstance().releaseVideoPlayer();
                }
            }
        });

    }

    private void initListener() {

    }

    private void initData() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        VideoPlayerManager.getInstance().releaseVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (VideoPlayerManager.getInstance().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}
