package com.huya.marksman.widget.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.charles.base.utils.ToastUtil;
import com.huya.marksman.R;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Jimmy on 2018/5/23 0023.
 */

public abstract class RecyclerViewHelper<T> extends RecyclerView.Adapter {

    private static final boolean DEBUG      = true;
    private static final String TAG         = RecyclerViewHelper.class.getSimpleName();
    public static final int FIRST_PAGE      = 1;
    public static final int PAGE_SIZE       = 20;

    public static final int LAYOUT_ALL      = -1;
    public static final int LAYOUT_LOADING  = 0;
    public static final int LAYOUT_EMPTY    = 1;
    public static final int LAYOUT_NETWORK  = 2;

    private int[] mLayoutIds = new int[] {
            R.layout.ui_default_loading,
            R.layout.ui_default_empty,
            R.layout.ui_default_network,
    };
    private View[] mLayouts = new View[mLayoutIds.length];
    private int mLoadMoreErrorStr = R.string.message_err;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshView;
    private NestedScrollView mNestedScrollView;

    private LoadDataListener<T> mLoadDataListener;
    private LoadDataListener<T> mExternalLoadDataListener;
    private List<T> mData = new ArrayList<>();
    private boolean mEverDataLoaded;

    private int mPageIndex = FIRST_PAGE;
    private int mPageSize = PAGE_SIZE;
    private int mDefaultPosition = 0;

    private boolean mEndOfData, mMoreLoading;
    private boolean mLoadingMoreDisabled;
    private boolean mLoadingMoreTipHidden;
    private LoadingMoreListener mLoadingMoreListener;

    private boolean mLoginCheckEnabled;
    private boolean mPreLoadedData;

    private OnItemClickListener mOnItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(RecyclerView recyclerView, View itemView, int position);
    }

    public interface LoadDataListener<T> {
        void onDataLoaded(int page, List<T> data);
        void onDataLoaded(int page, int position, List<T> data);
    }

    public RecyclerViewHelper<T> withRecyclerView(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        return this;
    }

    public RecyclerViewHelper<T> withRefreshView(SwipeRefreshLayout refreshView) {
        mRefreshView = refreshView;
        // using SwipeRefreshLayout's loading by default.
        mLayoutIds[LAYOUT_LOADING] = 0;
        return this;
    }

    public RecyclerViewHelper<T> withNestedScrollView(NestedScrollView scrollView) {
        mNestedScrollView = scrollView;
        mLayoutIds[LAYOUT_LOADING] = 0;
        return this;
    }

    public RecyclerViewHelper<T> withPageSize(int pageSize) {
        mPageSize = pageSize;
        return this;
    }

    public RecyclerViewHelper<T> withLoadingMoreDisabled(boolean disabled) {
        mLoadingMoreDisabled = disabled;
        return this;
    }

    public RecyclerViewHelper<T> withLoginCheckEnabled(boolean enabled) {
        mLoginCheckEnabled = enabled;
        return this;
    }

    public RecyclerViewHelper<T> withLoadingMoreTipHidden(boolean hidden) {
        mLoadingMoreTipHidden = hidden;
        return this;
    }

    public RecyclerViewHelper<T> withLayout(int which, int layoutId) {
        mLayoutIds[which] = layoutId;
        return this;
    }

    public RecyclerViewHelper<T> withLoadingMoreError(@StringRes int stringRes) {
        mLoadMoreErrorStr = stringRes;
        return this;
    }

    public RecyclerViewHelper<T> withOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public RecyclerViewHelper<T> withExternalLoadDataListener(LoadDataListener<T> listener) {
        mExternalLoadDataListener = listener;
        return this;
    }

    public RecyclerViewHelper<T> setup() {
        if (mRecyclerView == null) {
            throw new RuntimeException("null recycler view");
        }

        // refresh view
        if (mRefreshView != null) {
            mRefreshView.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    handleRefresh();
                }
            });
            mRefreshView.setColorSchemeResources(
                    R.color.colorPrimary,
                    android.R.color.holo_green_dark,
                    android.R.color.holo_orange_dark,
                    android.R.color.holo_blue_dark);
        }

        // loading more
        if (!mLoadingMoreDisabled) {
            mLoadingMoreListener = new LoadingMoreListener() {
                @Override
                void onLoadingMore() {
                    handleLoadingMore();
                }
            };

            if (mNestedScrollView != null) {
                mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                            mLoadingMoreListener.onLoadingMore();
                        }
                    }
                });
            }

            else if (mRecyclerView.getParent() instanceof NestedScrollView) {
                NestedScrollView nestedScrollView = (NestedScrollView) mRecyclerView.getParent();
                nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                            mLoadingMoreListener.onLoadingMore();
                        }
                    }
                });
            } else {
                mRecyclerView.addOnScrollListener(mLoadingMoreListener);
            }
        }

        // load data
        mLoadDataListener = new LoadDataListener<T>() {
            @Override
            public void onDataLoaded(int page, List<T> data) {
                if (page != mPageIndex) {
                    Timber.e(TAG, "data loaded with unexpected page");
                } else {
                    handleLoadedData(page, data != null ? data : new ArrayList<T>());
                }

                if (mExternalLoadDataListener != null) {
                    mExternalLoadDataListener.onDataLoaded(page, data);
                }
            }

            @Override
            public void onDataLoaded(int page, int position, List<T> data) {
                if (page != mPageIndex) {
                    Timber.e(TAG, "data loaded with unexpected page");
                } else {
                    handleLoadedData(page, position, data != null ? data : new ArrayList<T>());
                }

                if (mExternalLoadDataListener != null) {
                    mExternalLoadDataListener.onDataLoaded(page, position, data);
                }
            }
        };

        // adapter
        mRecyclerView.setAdapter(this);
        return this;
    }

    private void handleRefresh() {
        if (mMoreLoading) {
            // cancel loading more
            mMoreLoading = false;
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
        loadDataInternal(FIRST_PAGE);
    }

    private void handleLoadingMore() {
        if (mRefreshView != null) {
            // cancel refresh
            mRefreshView.setRefreshing(false);
        }
        mMoreLoading = true;
        mRecyclerView.getAdapter().notifyDataSetChanged();
        loadDataInternal(mPageIndex + 1);
    }

    public void loadData() {
        if (mRefreshView != null) {
            mRefreshView.setRefreshing(true);
        }
        if (mRecyclerView != null) {
            mEverDataLoaded = true;
            loadDataInternal(FIRST_PAGE);
        }
    }

    public int loadMoreData() {
        handleLoadingMore();
        return mPageIndex;
    }

    public void loadDataIfNeeded() {
        loadDataIfNeeded(FIRST_PAGE);
    }

    public void loadDataIfNeeded(int page) {
        if (!mEverDataLoaded && mRecyclerView != null) {
            mEverDataLoaded = true;
            loadDataInternal(page);
        }
    }

    private void loadDataInternal(int page) {
        hideLayout(LAYOUT_ALL);
        showLoadingView(page);
        mPageIndex = page;
        onLoadData(page, mLoadDataListener);
    }

    private void showLoadingView(int page) {
        if (page == FIRST_PAGE || mPageIndex == page) {
            if (mRefreshView != null) {
                mRefreshView.setRefreshing(true);
            }
        }
        if (dataSize() == 0 && mLayoutIds[LAYOUT_LOADING] != 0) {
            showLayout(LAYOUT_LOADING);
        }
    }

    private boolean isLayoutShowing(int which) {
        return mLayouts[which] != null && mLayouts[which].getVisibility() == View.VISIBLE;
    }

    private void onLayoutCreated(int which, View layout) {

    }

    private void showLayout(int which) {
        if (which == LAYOUT_ALL) {
            for (int i=0; i<mLayoutIds.length; i++) {
                showLayout(i);
            }
        } else if (mLayoutIds[which] > 0) {
            if (mLayouts[which] == null) {
                try {
                    Context context = mRecyclerView.getContext();
                    ViewGroup group = (ViewGroup) mRecyclerView.getParent();
                    View layout = LayoutInflater.from(context).inflate(
                            mLayoutIds[which], group, false);
                    group.addView(layout);
                    mLayouts[which] = layout;
                    onLayoutCreated(which, layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mLayouts[which] != null) {
                mLayouts[which].setVisibility(View.VISIBLE);
            }
        }
    }

    private void hideLayout(int which) {
        if (which == LAYOUT_ALL) {
            for (int i=0; i<mLayoutIds.length; i++) {
                hideLayout(i);
            }
        } else if (mLayouts[which] != null) {
            mLayouts[which].setVisibility(View.GONE);
        }
    }

    public void handleLoadedData(int page, List<T> data) {
        handleLoadedData(page, mDefaultPosition, data);
    }

    public void handleLoadedData(int page, int position, List<T> data) {
        int inserted = 0;
        if (data != null) {
            if (page == FIRST_PAGE && !mPreLoadedData) {
                mData.clear();
            }
            inserted = mData.size();
            mData.addAll(inserted, data);
            mEndOfData = data.size() < mPageSize;
        } else {
            mEndOfData = mData.size() == 0;
            Timber.e(TAG, "load data failed!");
        }

        if (DEBUG) Timber.d(TAG, "loaded data fileSize[%d] at page[%d], total fileSize[%d]",
                data==null ? 0 : data.size(),
                page,
                mData.size());

        mMoreLoading = false;

        if (mPreLoadedData) {
            mRecyclerView.getAdapter().notifyItemInserted(inserted);
        } else if (page == FIRST_PAGE || data == null || data.size() == 0) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        } else {
            mRecyclerView.getAdapter().notifyItemInserted(inserted);
        }

        if (position > 0) {
            mRecyclerView.scrollToPosition(position);
        }

        if (mLoadingMoreListener != null) {
            mLoadingMoreListener.disableLoadingMore(mEndOfData);
            mLoadingMoreListener.endLoadingMore();
        }

        // hide loading
        hideLayout(LAYOUT_LOADING);
        if (mRefreshView != null) {
            mRefreshView.setRefreshing(false);
        }

        // show certain layout if needed
        if (data == null && mData.size() == 0) { // error loading
            showLayout(LAYOUT_NETWORK);
        } else if (data == null) { // loading more error
            ToastUtil.show(mRecyclerView.getContext(), mLoadMoreErrorStr, Toast.LENGTH_SHORT);
        } else if (mData.size() == 0) { // empty
            showLayout(LAYOUT_EMPTY);
        } else { // content
            hideLayout(LAYOUT_ALL);
        }
    }

    // 预加载数据，mPageIndex不会累加
    public void handlePreLoadedData(int page, List<T> data) {
        mPreLoadedData = true;
        hideLayout(LAYOUT_ALL);
        if (mRefreshView != null) {
            mRefreshView.setRefreshing(true);
        }
        if (dataSize() == 0 && mLayoutIds[LAYOUT_LOADING] != 0) {
            showLayout(LAYOUT_LOADING);
        }
        mPageIndex = page;
        mData.addAll(0, data);
        mRecyclerView.getAdapter().notifyDataSetChanged();

        // hide loading
        hideLayout(LAYOUT_LOADING);
        if (mRefreshView != null) {
            mRefreshView.setRefreshing(false);
        }

        // show certain layout if needed
        if (data == null && mData.size() == 0) { // error loading
            showLayout(LAYOUT_NETWORK);
        } else if (data == null) { // loading more error
            ToastUtil.show(mRecyclerView.getContext(), mLoadMoreErrorStr, Toast.LENGTH_SHORT);
        } else if (mData.size() == 0) { // empty
            showLayout(LAYOUT_EMPTY);
        } else { // content
            hideLayout(LAYOUT_ALL);
        }
    }

    private int loadingMoreViewType() {
        return this.hashCode();
    }

    public abstract void onLoadData(int page, LoadDataListener<T> listener);
    public abstract int itemViewType(int position);
    public abstract @NonNull RecyclerView.ViewHolder handleCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    public abstract void handleBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder holder;
        if (viewType == loadingMoreViewType()) {
            holder = new LoadingMoreView(
                    inflater.inflate(R.layout.list_loading_more, parent, false));
        } else {
            holder = handleCreateViewHolder(parent, viewType);
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(mRecyclerView, v, (int) v.getTag());
                    }
                });
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == loadingMoreViewType()) {
            LoadingMoreView footer = (LoadingMoreView) holder;
            boolean hideTip = mLoadingMoreTipHidden || (mPageIndex==FIRST_PAGE && mEndOfData);
            footer.setTipHidden(hideTip);
            footer.setEndOfData(mEndOfData);
            footer.setLoading(mMoreLoading);
        } else {
            handleBindViewHolder(holder, position);
            holder.itemView.setTag(position);
        }
    }

    @Override
    public int getItemCount() {
        int count = mData.size();
        if (!mLoadingMoreDisabled && !mLoadingMoreTipHidden && count > 0) {
            count++;
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (!mLoadingMoreDisabled && !mLoadingMoreTipHidden && position == mData.size()) {
            return loadingMoreViewType();
        }
        return itemViewType(position);
    }

    public T dataAt(int position) {
        return mData.get(position);
    }

    public int dataSize() {
        return mData.size();
    }

    public List<T> getData() {
        return mData;
    }

    public boolean deleteItem(T t) {
        boolean removed = false;
        if (mData != null) {
            removed = mData.remove(t);
        }
        if (removed && mData.size() == 0) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
            showLayout(LAYOUT_EMPTY);
        }
        return removed;
    }

    public void clearData() {
        mData.clear();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        showLayout(LAYOUT_EMPTY);
    }

    public void removeData(List<T> toBeRemoved) {
        if (toBeRemoved != null && mData.removeAll(toBeRemoved)) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public boolean canLoadMore() {
        return !mEndOfData && !mLoadingMoreDisabled;
    }
}

