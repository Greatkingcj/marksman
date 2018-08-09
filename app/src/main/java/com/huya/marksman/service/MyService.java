package com.huya.marksman.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.huya.marksman.ipc.server.RemoteIpcServer;

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("MyService", "MyService: onCreate");
        RemoteIpcServer.setMyService(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public String switchVideo() {
        Log.e("MyService", "MyService: switchVideo");
        String ret = "hello myService";
        return ret;
    }

    public String updateSetting() {
        Log.e("MyService", "MyService: updateSetting");
        return "setting ok";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MyService", "MyService: onDestroy");
    }
}
