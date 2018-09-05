package com.huya.marksman.ui.video;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by niegangfeng on 2017/11/13.
 */
public class NonPageTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View page, float position) {
        page.setScaleX(0.999f);//hack
    }

    public static final ViewPager.PageTransformer INSTANCE = new NonPageTransformer();
}
