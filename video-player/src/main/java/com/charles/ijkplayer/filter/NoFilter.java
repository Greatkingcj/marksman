package com.charles.ijkplayer.filter;

import android.content.res.Resources;

/**
 * Created by charles on 2018/3/21.
 */

public class NoFilter extends AFilter{

    public NoFilter(Resources res) {
        super(res);
    }
    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh",
                "shader/base_fragment.sh");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
