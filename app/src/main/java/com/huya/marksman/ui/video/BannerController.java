package com.huya.marksman.ui.video;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import com.huya.marksman.R;
import com.huya.marksman.entity.ItemBanner;
import com.huya.marksman.util.ScreenUtil;
import com.huya.marksman.widget.viewpager.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by charles on 2018/9/5.
 */

public class BannerController {

    private BannerAdapter mBannerAdapter;
    private AutoScrollViewPager mTopBannerViewPage;
    Activity mActivity;

    public BannerController(Activity activity, View layout) {
        mActivity = activity;
        initTopBanner(layout);
    }

    private void initTopBanner(View layout) {
        mTopBannerViewPage = layout.findViewById(R.id.top_flipper);
        LinearLayout adDotIcon = layout.findViewById(R.id.ad_dot);
        mBannerAdapter = new BannerAdapter(mActivity, mTopBannerViewPage, adDotIcon);
        mTopBannerViewPage.addOnPageChangeListener(mBannerAdapter);
        int height = (int) (ScreenUtil.getScreenWidthPx() / 2.57);
        layout.findViewById(R.id.bannar_layout).setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        mTopBannerViewPage.setPageMargin(ScreenUtil.dp2px(9));
        mTopBannerViewPage.setPageTransformer(true, new ScalePageTransformer());
        mTopBannerViewPage.setAdapter(mBannerAdapter);
        mTopBannerViewPage.setInterval(4000);
        mTopBannerViewPage.setCycle(true);
        mTopBannerViewPage.setBorderAnimation(true);
        mTopBannerViewPage.setStopScrollWhenTouch(true);
        mTopBannerViewPage.setPageMargin(ScreenUtil.dp2px(5));
        mTopBannerViewPage.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
    }

    public void requestBannerData(String type) {
        //这里请求真实的数据
        List<ItemBanner> itemBanners = assemblyData();
        mBannerAdapter.setData(itemBanners);
        mTopBannerViewPage.startAutoScroll();
    }

    private List<ItemBanner> assemblyData() {
        List<ItemBanner> itemBanners = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            ItemBanner itemBanner = new ItemBanner();
            itemBanner.adImageUrl = "http://img.nimobox.com/smile/smile_files/201808/12/1534079699306/69.jpg";
            itemBanners.add(itemBanner);
        }
        return itemBanners;
    }

    public void resumeScroll() {
        if (mTopBannerViewPage != null) {
            mTopBannerViewPage.startAutoScroll();
        }
    }

    public void pauseScroll() {
        if (mTopBannerViewPage != null) {
            mTopBannerViewPage.stopAutoScroll();
        }
    }

    public boolean hasBannerData() {
        return mBannerAdapter != null && mBannerAdapter.getCount() > 0;
    }
}
