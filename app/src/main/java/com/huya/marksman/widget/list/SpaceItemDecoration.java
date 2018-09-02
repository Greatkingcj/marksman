package com.huya.marksman.widget.list;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Jimmy on 2018/5/22 0022.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private boolean addSpaceFirstItem;
    private boolean addSpaceLastItem;

    public SpaceItemDecoration(int space) {
        this(space, false, false);
    }

    public SpaceItemDecoration(int space, boolean addSpaceFirstItem, boolean addSpaceLastItem) {
        this.space = space;
        this.addSpaceFirstItem = addSpaceFirstItem;
        this.addSpaceLastItem = addSpaceLastItem;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (space <= 0) {
            return;
        }

        if (addSpaceFirstItem && parent.getChildLayoutPosition(view) < 1 || parent.getChildLayoutPosition(view) >= 1) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.top = space;
            } else {
                outRect.left = space;
            }
        }

        if (addSpaceLastItem && parent.getChildAdapterPosition(view) == getTotalItemCount(parent) - 1) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.bottom = space;
            } else {
                outRect.right = space;
            }
        }
    }

    private int getTotalItemCount(RecyclerView parent) {
        return parent.getAdapter().getItemCount();
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
        } else {
            throw new IllegalStateException("SpaceItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}
