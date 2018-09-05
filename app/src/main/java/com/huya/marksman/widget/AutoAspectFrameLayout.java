package com.huya.marksman.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.huya.marksman.R;


/**
 * Created by Administrator on 2018/5/22.
 */

public class AutoAspectFrameLayout extends FrameLayout {
    public static final int AUTO_ASPECT_NONE = 0;
    public static final int AUTO_ASPECT_WIDTH = 1;
    public static final int AUTO_ASPECT_HEIGHT = 2;

    private int mAutoAspect = AUTO_ASPECT_NONE;
    private float mAspectRatio = 1.f;

    public AutoAspectFrameLayout(Context context) {
        super(context);
    }

    public AutoAspectFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutoAspectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AutoAspectFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoAspectFrameLayout);
        mAutoAspect = array.getInt(R.styleable.AutoAspectFrameLayout_autoAspect, AUTO_ASPECT_NONE);
        mAspectRatio = array.getFloat(R.styleable.AutoAspectFrameLayout_aspectRatio, 1.f);
        array.recycle();
    }

    public void setAutoAspec(int autoAspect) {
        if (mAutoAspect != autoAspect) {
            mAutoAspect = autoAspect;
            requestLayout();
        }
    }

    public void setAspectRatio(float aspectRatio) {
        if (mAutoAspect != AUTO_ASPECT_NONE) {
            mAspectRatio = aspectRatio;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        if (mAutoAspect == AUTO_ASPECT_NONE) {
            super.onMeasure(widthSpec, heightSpec);
            return;
        }

        int widthMode = MeasureSpec.getMode(widthSpec);
        int heightMode = MeasureSpec.getMode(heightSpec);
        int widthSize = MeasureSpec.getSize(widthSpec);
        int heightSize = MeasureSpec.getSize(heightSpec);

        if (widthMode == MeasureSpec.EXACTLY && mAutoAspect == AUTO_ASPECT_WIDTH) {
            heightSpec = MeasureSpec.makeMeasureSpec((int) (widthSize / mAspectRatio), MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
            return;
        }

        if (heightMode == MeasureSpec.EXACTLY && mAutoAspect == AUTO_ASPECT_HEIGHT) {
            widthSpec = MeasureSpec.makeMeasureSpec((int) (heightSize * mAspectRatio), MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
            return;
        }

        super.onMeasure(widthSpec, heightSpec);

        widthSize = getMeasuredWidth();
        heightSize = getMeasuredHeight();

        if (mAutoAspect == AUTO_ASPECT_WIDTH) {
            heightSpec = MeasureSpec.makeMeasureSpec((int) (widthSize / mAspectRatio), MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
            return;
        }

        if (mAutoAspect == AUTO_ASPECT_HEIGHT) {
            widthSpec = MeasureSpec.makeMeasureSpec((int) (heightSize * mAspectRatio), MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
            return;
        }
    }
}
