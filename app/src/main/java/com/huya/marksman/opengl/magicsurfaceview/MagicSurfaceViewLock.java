package com.huya.marksman.opengl.magicsurfaceview;

public class MagicSurfaceViewLock {

    private static SimpleLock sLock = new SimpleLock();

    public static void lock() {
        sLock.lock();
    }

    public static void unlock() {
        sLock.unlock();
    }

}
