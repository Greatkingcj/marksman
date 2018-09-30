package com.charles.base.engine;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by charles on 2018/9/21.
 */

public class ThreadPool extends ThreadPoolExecutor{

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static long KEEP_ALIVE_TIME  = 60L;

    private final String name;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        String threadName = null;
        if (r instanceof Thread) {
            Thread thread = (Thread) r;
            threadName = thread.getName();
        }
        t.setName(name + "-thread-" + threadNumber.getAndIncrement() + "-" + (threadName != null ? threadName : "@" + r.hashCode()));
    }

    public ThreadPool(String name, int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue, boolean daemon) {
        super(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, new PoolThreadFactory(daemon));
        this.name = name;
    }

    public static ExecutorService newThreadPool(String name) {
        return newThreadPool(name, false);
    }

    public static ExecutorService newThreadPool(String name, boolean daemon) {
        int coreThreads = Math.min(NUMBER_OF_CORES * 2, 6);
        ThreadPool pool = new ThreadPool(name, coreThreads, Integer.MAX_VALUE, new SynchronousQueue<Runnable>(), daemon);
        return Executors.unconfigurableExecutorService(pool);
    }

    private static ExecutorService newThreadPool(String name, int coreThreads, int maxThread) {
        return newThreadPool(name, coreThreads, maxThread, false);
    }

    private static ExecutorService newThreadPool(String name, int coreThreads, int maxThread, boolean daemon) {
        ThreadPool pool = new ThreadPool(name, coreThreads, maxThread, new LinkedBlockingDeque<Runnable>(), daemon);
        return Executors.unconfigurableExecutorService(pool);
    }


    private static final class PoolThreadFactory implements ThreadFactory {

        private final boolean daemon;

        public PoolThreadFactory(boolean daemon) {
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r);

            t.setPriority(Process.THREAD_PRIORITY_BACKGROUND);

            t.setDaemon(daemon);

            return t;
        }
    }

}
