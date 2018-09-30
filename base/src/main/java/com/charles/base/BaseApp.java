package com.charles.base;

import android.app.Application;

import com.charles.base.engine.Engine;

/**
 * Created by charles on 2018/8/31.
 */

public abstract class BaseApp extends Application{

    private static BaseApp sApp;

    public static BaseApp application() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        Engine.create(this);
    }
}
