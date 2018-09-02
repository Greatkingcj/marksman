package com.charles.editor.entry;

import android.graphics.Rect;
import android.text.TextUtils;

import com.charles.editor.utils.VideoUtils;

import java.io.File;

/**
 * Created by charles on 2018/8/31.
 */

public class CutView {

    public String path;
    public VideoInfo info;

    public String audioPath;
    public long audioOffset;
    public Rect fullArea;
    public Rect cropArea;
    public long startPositon;
    public long clipDur;

    public static CutView withPath(String path) {
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            throw new RuntimeException("invalid video path");
        }

        CutView cutView = new CutView();
        cutView.path = path;
        cutView.info = VideoUtils.getInfo(path);
        return cutView;
    }
}
