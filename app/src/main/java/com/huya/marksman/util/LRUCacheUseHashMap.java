package com.huya.marksman.util;

import android.support.v4.util.LruCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by charles on 2018/8/1.
 */

public class LRUCacheUseHashMap {
    class Node {
        Node pre;
        Node next;

        Integer key;
        Integer val;

        Node(Integer k, Integer v) {
            key = k;
            val = v;
        }
    }
    
    Map<Integer, Node> map = new HashMap<Integer, Node>();
    Node head;
    Node tail;
    int cap;
    
    public LRUCacheUseHashMap(int capacity) {
        cap = capacity;
        head = new Node(null, null);
        tail = new Node(null, null);
        head.next = tail;
        tail.pre = head;
    }
    
    public int get(int key) {
        Node n = map.get(key);
        if (n != null) {
            n.pre.next = n.next;
            n.next.pre = n.pre;
            appendTail(n);
            return n.val;
        }
        return -1;
    }

    public void set(int key, int value) {
        Node n = map.get(key);

        if (n != null) {
            n.val = value;
            map.put(key, n);
            n.pre.next = n.next;
            n.next.pre = n.pre;
            appendTail(n);
            return;
        }

        if (map.size() ==cap) {
            Node tmp = head.next;
            head.next = head.next.next;
            head.next.pre = head;
            map.remove(tmp.key);
        }
        n = new Node(key, value);
        appendTail(n);
        map.put(key, n);

    }

    private void appendTail(Node n) {
        n.next = tail;
        n.pre = tail.pre;
        tail.pre.next = n;
        tail.pre = n;
    }
}
