package com.charles.ijkplayer.animatorview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.huya.ijkplayer.R;

import java.util.Random;

/**
 * Created by charles on 2018/4/3.
 */

public class LikeLayout extends FrameLayout{

    private Drawable icon;
    Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LikeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(Context context) {
        setClipChildren(false);
        icon = context.getDrawable(R.drawable.ic_heart);
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            addHeartView(x, y);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 在layout中添加红心，播放消失动画
     * @param x
     * @param y
     */
    private void addHeartView(float x, float y) {
        LayoutParams lp = new LayoutParams(icon.getIntrinsicWidth(),
                icon.getIntrinsicHeight());
        lp.leftMargin = (int) (x - icon.getIntrinsicWidth() / 2);
        lp.topMargin = (int) (y - icon.getIntrinsicHeight() / 2);
        final ImageView img = new ImageView(mContext);
        Matrix matrix = new Matrix();
        matrix.postRotate(getRandomRotate());
        img.setImageMatrix(matrix);
        img.setImageDrawable(icon);
        img.setLayoutParams(lp);
        addView(img);
        AnimatorSet animSet = getShowAnimSet(img);
        final AnimatorSet hideSet = getHideAnimSet(img);
        animSet.start();
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideSet.start();
            }
        });
        hideSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                removeView(img);
            }
        });
    }

    /**
     * 刚点击的时候的一个缩放效果
     * @param view
     * @return
     */
    private AnimatorSet getShowAnimSet(ImageView view) {
        AnimatorSet animSet = new AnimatorSet();
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f);
        animSet.playTogether(scaleX, scaleY);
        animSet.setDuration(100);
        return animSet;
    }

    /**
     * 缩放结束后到红心消失的效果
     * @param view
     * @return
     */
    private AnimatorSet getHideAnimSet(ImageView view) {
        AnimatorSet animSet = new AnimatorSet();
        //1.alpha动画
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0.1f);
        //2.缩放动画
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 2f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 2f);
        //3.translation动画
        ObjectAnimator translation = ObjectAnimator.ofFloat(view, "translationY", 0, -150);
        animSet.playTogether(alpha, scaleX, scaleY, translation);
        animSet.setDuration(500);
        return animSet;
    }

    private float getRandomRotate() {
        Random random = new Random();
        return random.nextInt(20) - 10;
    }
}
