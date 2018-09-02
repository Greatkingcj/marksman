package com.huya.marksman.ui.select;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.charles.base.utils.TimeUtil;
import com.charles.base.utils.imageloader.GlideApp;
import com.charles.editor.VideoEditorActivity;
import com.charles.editor.entry.VideoInfo;
import com.charles.editor.utils.ExecuteTimeAnalyzeUtil;
import com.huya.marksman.R;
import com.huya.marksman.util.ScreenUtil;
import com.huya.marksman.widget.list.RecyclerViewHelper;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import com.tbruyelle.rxpermissions2.RxPermissions;

import static com.huya.marksman.widget.list.RecyclerViewHelper.PAGE_SIZE;

public class LocalVideoActivity extends AppCompatActivity {

    public static final int VIDEO_HEADER = 0;
    public static final int VIDEO_ITEM = 1;
    public static final int COLUMN_NUMBER = 3;

    //PX
    public static final int COLUMN_PADDING = ScreenUtil.dp2px(4);

    RecyclerView mRecyclerView;
    RecyclerViewHelper mRecyclerViewHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        gridLayoutManager.setSpanSizeLookup(new VideoSpanSizeLookup());
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new VideoItemDecoration(ScreenUtil.dp2px(COLUMN_PADDING)));
        mRecyclerViewHelper = new RecyclerViewHelper<Object>() {
            @Override
            public int itemViewType(int position) {
                return ((VideoInfo)mRecyclerViewHelper.getData().get(position)).isHeader
                        ? VIDEO_HEADER : VIDEO_ITEM;
            }

            @Override
            public void onLoadData(int page, LoadDataListener<Object> listener) {
                LocalVideoActivity.this.loadData(page, listener);
            }

            @NonNull
            @Override
            public RecyclerView.ViewHolder handleCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                if (viewType == VIDEO_HEADER) {
                    return new Header(inflater.inflate(R.layout.item_video_title, null));
                } else {
                    Holder holder = new Holder(inflater.inflate(R.layout.item_video_select , null));
                    return holder;
                }
            }

            @Override
            public void handleBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
                VideoInfo videoInfo = (VideoInfo) (dataAt(position));
                if (holder.getItemViewType() == VIDEO_HEADER) {
                    ((Header)holder).textView.setText(videoInfo.date);
                } else {
                    ((Holder)holder).bindData(videoInfo);
                }

            }
        }.withRecyclerView(mRecyclerView)
                .withPageSize(PAGE_SIZE)
                .withLoadingMoreDisabled(true)
                .setup();
        mRecyclerViewHelper.loadData();
    }

    private void loadData(int page, RecyclerViewHelper.LoadDataListener<Object> listener) {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(granted -> {
            if (granted) {
                Observable.fromCallable(()->{
                    ExecuteTimeAnalyzeUtil.start();
                    List<VideoInfo> result = new VideoProvider().loadVideoList();
                    ExecuteTimeAnalyzeUtil.tag("after loadVideoList");
                    return result;

                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(selectVideos -> {
                    List<Object> data = new ArrayList<>();
                    data.addAll(selectVideos);
                    Log.e("LocalVideoActivity", "CreatedWallpaper:loadData " + data.size());
                    listener.onDataLoaded(page, data);
                });
            } else {
                finish();
            }
        });
    }


    class VideoSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        public VideoSpanSizeLookup() {

        }

        @Override
        public int getSpanSize(int position) {
            switch (mRecyclerViewHelper.getItemViewType(position)) {
                case VIDEO_HEADER:
                    return COLUMN_NUMBER;
                case VIDEO_ITEM:
                    return 1;
                default:
                    return -1;
            }
        }
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RelativeLayout container;
        ImageView mCover;
        TextView mDuration;
        VideoInfo mVideo;

        public Holder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.video_view);
            mCover = itemView.findViewById(R.id.cover_image);
            mDuration = itemView.findViewById(R.id.duration);
            itemView.setOnClickListener(this);
        }

        public void bindData(VideoInfo video) {
            mVideo = video;
            mDuration.setText(TimeUtil.convertSecondsToTime((int)Math.ceil(mVideo.duration / 1000.0f)));
            loadThumb();
        }

        public void loadThumb() {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mCover.getLayoutParams();
            params.width = (ScreenUtil.getScreenWidthPx() + 2 * COLUMN_PADDING * COLUMN_NUMBER ) / 3;
            params.height = params.width;
            mCover.setLayoutParams(params);
            GlideApp.with(LocalVideoActivity.this).load(mVideo.videoPath).placeholder(R.drawable.place_holder_video).into(mCover);

        }

        @Override
        public void onClick(View v) {
            VideoEditorActivity.launch(LocalVideoActivity.this , mVideo);
        }

    }

    class Header extends RecyclerView.ViewHolder {

        TextView textView;

        public Header(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
        }
    }
}
