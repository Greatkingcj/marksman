package com.charles.base.engine;


import com.charles.base.BaseApp;

import java.util.concurrent.ExecutorService;

/**
 * Created by Jimmy on 2017/2/8 0008.
 */
public class Engine {

    private static Engine sInstance;

    private static ExecutorService sThreadPool;

    private BaseApp mApp;

    public static synchronized void create(BaseApp app) {
        if (sInstance == null) {
            sThreadPool = ThreadPool.newThreadPool("App-Engine", false);
            sInstance = new Engine(app);
        }
    }

    public static Engine instance() {
        if (sInstance == null) {
            throw new RuntimeException("Engine not created");
        }
        return sInstance;
    }

    private Engine(BaseApp app) {
        mApp = app;
    }

    public ExecutorService getThreadPool() {
        return sThreadPool;
    }
}
