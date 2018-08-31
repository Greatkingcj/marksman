package com.charles.ijkplayer.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.huya.ijkplayer.R;

public class Camera1Activity extends AppCompatActivity {
    private Button camera_basic;
    private Button camera_anim;
    private Button camera_beau;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera1);

        findViewById(R.id.btn_camera_basic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Camera1Activity.this, CameraBasicActivity.class);
                Camera1Activity.this.startActivity(intent);
            }
        });

        findViewById(R.id.btn_camera_anim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Camera1Activity.this, CameraAnimActivity.class);
                Camera1Activity.this.startActivity(intent);
            }
        });

        findViewById(R.id.btn_camera_beau).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Camera1Activity.this, CameraBeauActivity.class);
                Camera1Activity.this.startActivity(intent);
            }
        });

    }
}
