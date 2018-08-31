package com.charles.ijkplayer.proxy;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by charles on 2018/3/15.
 */

public class ProxyManager {
    private static HttpProxyCacheServer proxyCacheServer = null;

    public static HttpProxyCacheServer getProxy(Context context) {
        if (proxyCacheServer == null) {
            proxyCacheServer = newProxy(context);
        }
        return proxyCacheServer;
    }

    public static HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(200 * 1024 * 1024)
                .build();
    }
}
