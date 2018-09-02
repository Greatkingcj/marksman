package com.charles.base.engine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Jimmy on 2017/2/8 0008.
 */
public class ThreadPool extends ThreadPoolExecutor {

    public static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    public static final long KEEP_ALIVE_TIME = 60L; // in seconds

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String name;

    public ThreadPool(String name, int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> workQueue, boolean daemon) {
        super(corePoolSize, maximumPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, new PoolThreadFactory(daemon));
        this.name = name;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        String threadName = null;
        if (r instanceof Thread) {
            Thread thread = (Thread) r;
            threadName = thread.getName();
        }

        t.setName(name + "-thread-" + threadNumber.getAndIncrement() + "-" + (threadName != null ? threadName : "@" + r.hashCode()));
    }

    public static ExecutorService newThreadPool(String name, int coreThreads, int maxThreads, boolean daemon) {
        ThreadPool pool = new ThreadPool(name, coreThreads, maxThreads, new LinkedBlockingQueue<Runnable>(), daemon);
        return Executors.unconfigurableExecutorService(pool);
    }

    public static ExecutorService newThreadPool(String name, int coreThreads, int maxThreads) {
        return newThreadPool(name, coreThreads, maxThreads, false);
    }

    public static ExecutorService newThreadPool(String name, boolean daemon) {
        int coreThreads = Math.min(NUMBER_OF_CORES*2, 6);
        ThreadPool pool = new ThreadPool(name, coreThreads, Integer.MAX_VALUE, new SynchronousQueue<Runnable>(), daemon);
        return Executors.unconfigurableExecutorService(pool);
    }

    public static ExecutorService newThreadPool(String name) {
        return newThreadPool(name, false);
    }

    private static final class PoolThreadFactory implements ThreadFactory {

        private final boolean daemon;

        public PoolThreadFactory(boolean daemon) {
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);

            t.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            t.setDaemon(daemon);

            return t;
        }
    }
}