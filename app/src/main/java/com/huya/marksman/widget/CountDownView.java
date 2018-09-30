package com.huya.marksman.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.charles.base.utils.UIUtil;
import com.huya.marksman.R;


public class CountDownView extends View {
    //圆轮颜色
    private int mRingBgColor, mRoundProgressColor;
    //圆轮宽度
    private float mRingWidth;
    //圆轮进度值文本大小
    private boolean showRingBg, mShowText;
    private int mRingProgessTextSize;
    //宽度
    private int mWidth;
    //高度
    private int mHeight;
    private Paint mPaint;
    //圆环的矩形区域
    private RectF mRectF;
    //
    private int mProgessTextColor;
    private int mCountdownTime, mEscapedTime;
    private float mCurrentProgress;
    private OnCountDownFinishListener mListener;

    private ValueAnimator mValueAnimator;
    private Typeface typeFace;
    protected static Handler handler = new Handler();
    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        if (a != null) {
            showRingBg = a.getBoolean(R.styleable.CountDownView_showRingBg, false);
            mRingBgColor = a.getColor(R.styleable.CountDownView_ringBgColor, context.getResources().getColor(R.color.colorPrimaryDark));
            mRoundProgressColor = a.getColor(R.styleable.CountDownView_roundProgressColor, context.getResources().getColor(R.color.colorAccent));
            mRingWidth = UIUtil.dipToPx(context, a.getFloat(R.styleable.CountDownView_ringWidth, 10));

            mShowText = a.getBoolean(R.styleable.CountDownView_showText, false);
            mRingProgessTextSize = a.getDimensionPixelSize(R.styleable.CountDownView_progressTextSize, UIUtil.dipToPx(context, 12));
            mProgessTextColor = a.getColor(R.styleable.CountDownView_progressTextColor, context.getResources().getColor(R.color.colorAccent));
            mCountdownTime = a.getInteger(R.styleable.CountDownView_countdownTime, 30);
            a.recycle();
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE); //设置空心
        mPaint.setStrokeWidth(mRingWidth); //设置圆环的宽度
        this.setWillNotDraw(false);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            mRectF = new RectF(0 + mRingWidth / 2, 0 + mRingWidth / 2, mWidth - mRingWidth / 2, mHeight - mRingWidth / 2);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mWidth == 0) {
            mWidth = getWidth();
            mHeight = getHeight();
            mRectF = new RectF(0 + mRingWidth / 2, 0 + mRingWidth / 2, mWidth - mRingWidth / 2, mHeight - mRingWidth / 2);
        }
        if (showRingBg) {
            int centre = getWidth() / 2; //获取圆心的x坐标
            int radius = (int) (centre - mRingWidth / 2); //圆环的半径
            mPaint.setColor(mRingBgColor); //设置圆环的颜色
            canvas.drawCircle(centre, centre, radius, mPaint); //画出圆环
        }

        mPaint.setColor(mRoundProgressColor);
        canvas.drawArc(mRectF, -90, mCurrentProgress - 360, false, mPaint);

        //绘制文本
        if (mShowText) {
            Paint textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextAlign(Paint.Align.CENTER);
            String text = String.valueOf((mCountdownTime - mEscapedTime)/1000);
            textPaint.setTextSize(mRingProgessTextSize);
            textPaint.setColor(mProgessTextColor);
            if (typeFace != null) {
                textPaint.setTypeface(typeFace);
            }
            //文字居中显示
            Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
            int baseline = (int) ((mRectF.bottom + mRectF.top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(text, mRectF.centerX(), baseline, textPaint);
        }
    }

    private ValueAnimator getValA(long countdownTime) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
        valueAnimator.setDuration(countdownTime);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setRepeatCount(0);
        return valueAnimator;
    }

    public void setTypeface(Typeface typeFace) {
        this.typeFace = typeFace;
    }

    /**
     * 开始倒计时
     */
    public void startCountDown(int second) {
        mCountdownTime = second * 1000;
        mEscapedTime = 0;
        new Runnable() {
            @Override
            public void run() {
                mCurrentProgress = (int) (360 * ((float)mEscapedTime / mCountdownTime));
                invalidate();
                mEscapedTime += 50;
                if (mEscapedTime < mCountdownTime) {
                    handler.postDelayed(this, 50);
                } else {
                    setVisibility(GONE);
                }
            }
        }.run();
    }

    public void endCountDown() {
        handler.removeCallbacksAndMessages(null);
        setVisibility(GONE);
    }

    public void setAddCountDownListener(OnCountDownFinishListener mListener) {
        this.mListener = mListener;
    }

    public interface OnCountDownFinishListener {
        void countDownFinished();
    }
}
