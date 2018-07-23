package com.huya.huyaijkplayer.manager;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by charles on 2018/3/15.
 */

public class AndroidVideoCacheManager {
    private static HttpProxyCacheServer proxyCacheServer = null;

    public static HttpProxyCacheServer getProxy(Context context) {
        if (proxyCacheServer == null) {
            proxyCacheServer = new HttpProxyCacheServer(context);
        }
        return proxyCacheServer;
    }
}
