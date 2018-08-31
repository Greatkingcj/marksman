package com.charles.ijkplayer.activitys;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.charles.ijkplayer.controller.HuyaPlayerController;
import com.charles.ijkplayer.view.videoview.HuyaVideoView;
import com.huya.ijkplayer.R;

import java.util.List;

/**
 * Created by charles on 2018/3/25.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private Context mContext;
    private List<Video> mVideoList;

    VideoAdapter(Context context, List<Video> videos) {
        mContext = context;
        mVideoList = videos;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.videolist_recycler_item, parent, false);
        VideoViewHolder holder = new VideoViewHolder(itemView);

        //创建视频播放器， 主要只要创建一次就可以
        HuyaPlayerController controller = new HuyaPlayerController(mContext);
        holder.setController(controller);
        return holder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = mVideoList.get(position);
        holder.bindData(video);
    }

    @Override
    public int getItemCount() {
        return mVideoList == null ? 0 : mVideoList.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        HuyaPlayerController mController;
        HuyaVideoView mVideoView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            mVideoView = itemView.findViewById(R.id.video_view_item);
        }

        void setController(HuyaPlayerController controller) {
            mController = controller;
            mVideoView.setController(mController);
        }

        void bindData(Video video) {
            mController.setTitle(video.getTitle());
            mVideoView.setVideoPath(video.getVideoUrl());
        }
    }
}
