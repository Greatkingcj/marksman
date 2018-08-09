package com.huya.marksman.util;

import android.support.v4.util.LruCache;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by charles on 2018/8/1.
 */

public class LRUCacheUseLinkedHashMap {
    private int capacity;
    private Map<Integer, Integer> cache;

    public LRUCacheUseLinkedHashMap(int capacity) {
        this.capacity = capacity;
        this.cache = new LinkedHashMap<Integer, Integer>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Entry<Integer, Integer> eldest) {
                return size() > capacity;
            }
        };
    }

    public int get(int key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        } else {
            return -1;
        }
    }

    public void set(int key, int value) {
        cache.put(key, value);
    }

}
