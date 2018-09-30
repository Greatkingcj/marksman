package com.huya.marksman.ui.mine;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huya.marksman.R;
import com.huya.marksman.widget.CountDownView;

public class TestView1Activity extends AppCompatActivity {

    CountDownView countDownView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_view1);


        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        countDownView = findViewById(R.id.count_view);
        countDownView.setAddCountDownListener(new CountDownView.OnCountDownFinishListener() {
            @Override
            public void countDownFinished() {
                countDownView.endCountDown();
            }
        });
        countDownView.startCountDown(10);

    }
}
