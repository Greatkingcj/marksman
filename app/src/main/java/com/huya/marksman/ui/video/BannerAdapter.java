package com.huya.marksman.ui.video;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.huya.marksman.R;
import com.huya.marksman.entity.ItemBanner;
import com.huya.marksman.util.ScreenUtil;
import com.huya.marksman.util.StringUtil;
import com.huya.marksman.widget.imageloader.GlideImageLoader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by charles
 */

public class BannerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private Activity mContext;
    private List<ItemBanner> mTopBannerData = new ArrayList<ItemBanner>();
    private LinkedList<View> mViews = new LinkedList<View>();
    private ViewPager mTopBannerViewPager;
    private ChangeCallback mCallback;
    private LinearLayout mAdDotIcon;
    private int mCurrentItem;
    private int count;

    public BannerAdapter(Activity activity, ViewPager viewPager, LinearLayout adDot) {
        mContext = activity;
        mTopBannerViewPager = viewPager;
        mAdDotIcon = adDot;
        mCallback = new BannerChangeCallback();
    }

    public void setData(List<ItemBanner> data) {
        if (data != null && data.size() > 0) {
            mTopBannerData.clear();
            mTopBannerData.addAll(data);
            initImageView();
            bindTopViewPageAndDotView();
            mTopBannerViewPager.setCurrentItem(1);
            count = data.size();
        }
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View view = mViews.get(position);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ItemBanner info = getItem(position);
                if (info != null) {

                }
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentItem = position;

        if (mCallback != null && position >= 1) {
            if (position == count + 1) {
                mCallback.execute(0);
            } else {
                mCallback.execute(position - 1);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case 0:
                //No operation
                if (mCurrentItem == 0) {
                    mTopBannerViewPager.setCurrentItem(count, false);
                } else if (mCurrentItem == count + 1) {
                    mTopBannerViewPager.setCurrentItem(1, false);
                }
                break;
            case 1:
                //start Sliding
                if (mCurrentItem == count + 1) {
                    mTopBannerViewPager.setCurrentItem(1, false);
                } else if (mCurrentItem == 0) {
                    mTopBannerViewPager.setCurrentItem(count, false);
                }
                break;
            case 2:
                //end Sliding
                break;
            default:
                break;
        }
    }

    public ItemBanner getItem(int position) {
        try {
            if (position == 0) {
                return mTopBannerData.get(position);
            } else {
                return mTopBannerData.get(position - 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initImageView() {
        if (mTopBannerData.size() > 0) {
            mViews.clear();
            setView(mTopBannerData.get(mTopBannerData.size() - 1));
            for (ItemBanner data : mTopBannerData) {
                setView(data);
            }
            setView(mTopBannerData.get(0));
        }
        notifyDataSetChanged();
    }

    public void setView(ItemBanner info) {
        String imageUrl = info.adImageUrl;
        final ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setPadding(0 , 0 , 0 ,0);
        if (StringUtil.isNull(imageUrl) || !imageUrl.contains("://")) {
            imageView.setImageResource(R.drawable.place_holder_banner);
        } else {
            GlideImageLoader.loadImage(mContext,
                    imageView,
                    imageUrl,
                    R.drawable.place_holder_ad);
        }

        mViews.add(imageView);
    }

    interface ChangeCallback {
        void execute(Object... obj);
    }

    class BannerChangeCallback implements ChangeCallback {

        @Override
        public void execute(Object... obj) {
            if (obj != null) {
                Integer position = Integer.valueOf(obj[0].toString());
                if (position != null) {
                    switchDotView(position);
                }
            }
        }
    }

    private void switchDotView(int position) {
        try {
            if (null != mAdDotIcon) {
                int count = mAdDotIcon.getChildCount();
                if (count > 0) {
                    for (int i = 0; i < count; i++) {
                        ImageView localImageView = (ImageView) mAdDotIcon.getChildAt(i);
                        localImageView.setImageResource(R.drawable.shape_circle_grey_point);
                    }
                    ImageView localImageView = (ImageView) mAdDotIcon.getChildAt(position);
                    localImageView.setImageResource(R.drawable.shape_circle_yellow_point);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindTopViewPageAndDotView() {
        if (mAdDotIcon != null && mTopBannerData != null && mTopBannerData.size() > 0) {
            mAdDotIcon.removeAllViews();
            int size = mTopBannerData.size();
            if (size > 1) {
                for (int i = 0; i < size; i++) {
                    ImageView imageView = new ImageView(mContext);
                    if (i == 0) {
                        imageView.setImageResource(R.drawable.shape_circle_yellow_point);
                    } else {
                        imageView.setImageResource(R.drawable.shape_circle_grey_point);
                    }

                    imageView.setPadding(ScreenUtil.dp2px(4), 0, ScreenUtil.dp2px(4), 0);
                    mAdDotIcon.addView(imageView);
                }
            }

        }
    }
}
