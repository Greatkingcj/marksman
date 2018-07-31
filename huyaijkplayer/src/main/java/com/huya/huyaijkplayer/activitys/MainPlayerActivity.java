package com.huya.huyaijkplayer.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.huya.huyaijkplayer.R;

public class MainPlayerActivity extends AppCompatActivity {
    private ListView mFileListView;
    private SampleMediaAdapter mAdapter;
    private Button mSurface;
    private Button mHuyaView;
    private Button mListView;
    private Button mOpenGLES;
    private Button mCamera;
    private Button mCamera2;
    private Button mLike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_main);

        mHuyaView = findViewById(R.id.btn_huya_view);
        mHuyaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, HuyaVideoViewActivity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mListView = findViewById(R.id.btn_view_list);
        mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, VideoListActivity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mSurface = findViewById(R.id.btn_surface);
        mSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, SurfaceViewActivity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mOpenGLES = findViewById(R.id.btn_opengles);
        mOpenGLES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, OpenGlActivity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mCamera = findViewById(R.id.btn_camera);
        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, CameraActivity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mCamera2 = findViewById(R.id.btn_camera2);
        mCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, Camera1Activity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mLike = findViewById(R.id.btn_like);
        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPlayerActivity.this, AnimatorActivity.class);
                MainPlayerActivity.this.startActivity(intent);
            }
        });

        mFileListView = findViewById(R.id.lv_play_item);
        mAdapter = new SampleMediaAdapter(this);
        mFileListView.setAdapter(mAdapter);
        mFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SampleMediaItem item = mAdapter.getItem(position);
                String name = item.mName;
                String url = item.mUrl;
                VideoActivity.intentTo(MainPlayerActivity.this, url, name);
            }
        });
        mAdapter.addItem("http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4", "深圳环保");
    }

    final class SampleMediaItem {
        String mUrl;
        String mName;

        public SampleMediaItem(String url, String name) {
            mUrl = url;
            mName = name;
        }
    }

    final class SampleMediaAdapter extends ArrayAdapter<SampleMediaItem> {

        public SampleMediaAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
        }

        public void addItem(String url, String name) {
            add(new SampleMediaItem(url, name));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new ViewHolder();
                viewHolder.mNameTextView = (TextView) view.findViewById(android.R.id.text1);
                viewHolder.mUrlTextView = (TextView) view.findViewById(android.R.id.text2);
            }

            SampleMediaItem item = getItem(position);
            viewHolder.mNameTextView.setText(item.mName);
            viewHolder.mUrlTextView.setText(item.mUrl);

            return view;
        }
    }

    final class ViewHolder {
        public TextView mNameTextView;
        public TextView mUrlTextView;
    }
}
