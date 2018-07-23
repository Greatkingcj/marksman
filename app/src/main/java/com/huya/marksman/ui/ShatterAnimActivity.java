package com.huya.marksman.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.huya.marksman.R;
import com.huya.marksman.widget.ShatterAnimLayout;

/**
 * @author charles
 */
public class ShatterAnimActivity extends AppCompatActivity {
    private ShatterAnimLayout shatterAnimLayout;
    private Button startAnimationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shatter_anim);
        initView();
    }

    private void initView() {
        shatterAnimLayout = findViewById(R.id.sa_layout);
        startAnimationButton = findViewById(R.id.btn_start_animation);
        startAnimationButton.setOnClickListener(v -> startAnimation());
    }

    private void startAnimation() {
        shatterAnimLayout.startAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (shatterAnimLayout != null) {
            shatterAnimLayout.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (shatterAnimLayout != null) {
            shatterAnimLayout.onPause();
        }
    }
}
