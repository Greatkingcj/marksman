package com.huya.marksman;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * @author charles
 */
public class SplashActivity extends AppCompatActivity {
    private Handler handler;
    private DelayRunnable delayRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        delayRunnable = new DelayRunnable();
        handler.postDelayed(delayRunnable, 1500);
    }

    private class DelayRunnable implements Runnable {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(delayRunnable);
    }
}
