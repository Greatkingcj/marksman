package com.huya.marksman.ui.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.huya.marksman.R;
import com.huya.marksman.ui.user.UserListActivity;
import com.huya.marksman.ui.user.architecturecomponents.UserListAACActivity;
import com.huya.marksman.ui.webview.WebViewActivity;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void  showUsers(View view) {
        startActivity(new Intent(this, UserListActivity.class));
    }

    public void  showUsersAAC(View view) {
        startActivity(new Intent(this, UserListAACActivity.class));
    }

    public void  showWebView(View view) {
        startActivity(new Intent(this, WebViewActivity.class));
    }

    public void  testService(View view) {
        startActivity(new Intent(this, TestServiceActivity.class));
    }

    public void  testTextureView(View view) {
        startActivity(new Intent(this, TestTextureViewActivity.class));
    }
}
