package com.huya.marksman.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.huya.marksman.R;
import com.huya.marksman.widget.PanoSurfaceView;

public class PanoActivity extends AppCompatActivity {

    PanoSurfaceView panoSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pano);
        panoSurfaceView = findViewById(R.id.pano_surface_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        panoSurfaceView.onResume();
    }
}
