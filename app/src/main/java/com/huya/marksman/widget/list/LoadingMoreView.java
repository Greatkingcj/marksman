package com.huya.marksman.widget.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.huya.marksman.R;


/**
 * Created by Jimmy on 2018/5/23 0023.
 */

public class LoadingMoreView extends RecyclerView.ViewHolder {

    private View mLoadingBar;
    private TextView mTip;
    private boolean mTipHidden;

    public LoadingMoreView(View itemView) {
        super(itemView);
        mLoadingBar = itemView.findViewById(R.id.loading_bar);
        mTip = itemView.findViewById(R.id.tip);
    }

    public void setEndOfData(boolean endOfData) {
        if (endOfData) {
            mLoadingBar.setVisibility(View.INVISIBLE);
            mTip.setVisibility(View.VISIBLE);
        } else {
            mLoadingBar.setVisibility(View.VISIBLE);
            mTip.setVisibility(View.INVISIBLE);
        }
    }

    public void setLoading(boolean loading) {
        if (loading) { // loading
            itemView.setVisibility(View.VISIBLE);
        } else if (mTip.getVisibility() == View.VISIBLE) { // not loading but tip showing
            itemView.setVisibility(mTipHidden ? View.INVISIBLE : View.VISIBLE);
        } else { // not loading without tip, invisible
            itemView.setVisibility(View.INVISIBLE);
        }
    }

    public void setTipHidden(boolean hidden) {
        mTipHidden = hidden;
        if (mTip.getVisibility() == View.VISIBLE) {
            itemView.setVisibility(mTipHidden ? View.INVISIBLE : View.VISIBLE);
        }
    }
}
