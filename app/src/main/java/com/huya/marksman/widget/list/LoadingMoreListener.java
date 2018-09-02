package com.huya.marksman.widget.list;

import android.support.v7.widget.RecyclerView;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * Created by Jimmy on 2018/5/23 0023.
 */

public abstract class LoadingMoreListener extends RecyclerView.OnScrollListener {

    private boolean mLoadingMoreDisabled;
    private boolean mLoading;

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (!recyclerView.canScrollVertically(1) && newState == SCROLL_STATE_IDLE) {
            loadingMore();
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    private void loadingMore() {
        if (!mLoading && !mLoadingMoreDisabled) {
            mLoading = true;
            onLoadingMore();
        }
    }

    public void disableLoadingMore(boolean disabled) {
        mLoadingMoreDisabled = disabled;
    }

    public void endLoadingMore() {
        mLoading = false;
    }

    abstract void onLoadingMore();
}
