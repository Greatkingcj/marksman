package com.huya.marksman.ui.webview;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 *
 * @author charles
 * @date 2018/7/27
 */

public class MaskScriptInterface {
    private static final String TAG = "MaskScriptInterface";

    private JsBridge jsBridge;

    public MaskScriptInterface(JsBridge jsBridge) {
        this.jsBridge = jsBridge;
    }
    @JavascriptInterface
    public void setValue(String str){
        Log.e(TAG, str);
        jsBridge.setTextViewValue(str);
    }
}
