package com.huya.marksman.ui.wallpaper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huya.marksman.R;
import com.huya.marksman.service.wallpaper.PanoWallpaperService;
import com.huya.marksman.ui.ParticlesActivity;

public class WallpaperActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper);
    }

    public void  gotoParticles(View view) {
        startActivity(new Intent(this, ParticlesActivity.class));
    }

    public void  gotoPano(View view) {
        //startActivity(new Intent(this, PanoActivity.class));
        PanoWallpaperService.setWallpaperWithImage(this, "");
    }
}
