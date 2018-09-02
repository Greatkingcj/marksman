package com.huya.marksman.ui.select;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class VideoItemDecoration extends RecyclerView.ItemDecoration{

  private int mSpace;

  public VideoItemDecoration(int space) {
    this.mSpace = space ;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

    if (parent.getPaddingLeft() != mSpace) {
      parent.setPadding(mSpace, mSpace, mSpace, mSpace);
      parent.setClipToPadding(false);
    }

    outRect.bottom = mSpace;
    outRect.left = mSpace;
    outRect.right = mSpace;
  }
}
