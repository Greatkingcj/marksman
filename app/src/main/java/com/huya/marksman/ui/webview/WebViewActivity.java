package com.huya.marksman.ui.webview;

import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huya.marksman.R;

public class WebViewActivity extends AppCompatActivity implements JsBridge{

    private WebView mWebView;
    private TextView outPutString;
    private EditText inputString;
    private Button send;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        initView();
        initSettings();
    }

    private void initView() {
        mWebView = findViewById(R.id.web_view);
        outPutString = findViewById(R.id.tv_from_web_view);
        inputString = findViewById(R.id.edit_input);
        send = findViewById(R.id.btn_send);
        mHandler = new Handler();
    }

    private void initSettings() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new MaskScriptInterface(this), "MaskScript");
        mWebView.loadUrl("file:///android_asset/index.html");
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = inputString.getText().toString();
                mWebView.loadUrl("javascript:if(window.remote){window.remote('" + str +"')}");
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @Override
    public void setTextViewValue(String value) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                outPutString.setText(value);
            }
        });
    }
}
