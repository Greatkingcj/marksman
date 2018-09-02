package com.charles.base.engine;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;
import android.os.Looper;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * 对比创建一个 MainLooper Handler 的优势:
 *
 * 当 {@link Activity#onDestroy()} 时,
 * 调用 {@link #shutdownNow()} 可消极避免诸如 Dialog 在 Activity 结束后才弹出时抛出的{@link android.view.WindowManager.BadTokenException}.
 * 简单来说,就是与Activity生命周期同步.
 *
 * @author rj-liang
 * @date 2018/5/14 16:09
 */
public class MainThreadExecutor implements Executor, LifecycleObserver {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private LifecycleOwner mLifecycleOwner;
    private List<Runnable> mTasks;

    private boolean isShutdown = false;

    private MainThreadExecutor(LifecycleOwner owner) {
        mTasks = Collections.synchronizedList(new LinkedList<Runnable>());
        mLifecycleOwner = owner;
        mLifecycleOwner.getLifecycle().addObserver(this);
    }

    public static MainThreadExecutor newInstance(LifecycleOwner owner) {
        return new MainThreadExecutor(owner);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onStateChange() {
        if (mLifecycleOwner.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
            shutdownNow();
        }
    }

    @Override
    public synchronized void execute(final Runnable r) {
        if (!isShutdown) {
            mTasks.add(r);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTasks.remove(r);
                    r.run();
                }
            });
        }
    }

    public synchronized void executeDelayed(final Runnable r, long delayMillis) {
        if (!isShutdown) {
            mTasks.add(r);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mTasks.remove(r);
                    r.run();
                }
            }, delayMillis);
        }
    }

    public synchronized List<Runnable> shutdownNow() {
        isShutdown = true;
        Iterator<Runnable> iterator = mTasks.iterator();
        while (iterator.hasNext()) {
            Runnable task = iterator.next();
            mHandler.removeCallbacks(task);
        }

        return Collections.emptyList();
    }
}
