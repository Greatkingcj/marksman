package com.charles.ijkplayer.controller;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.charles.ijkplayer.view.videoview.BaseVideoPlayer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by charles on 2018/3/14.
 */

public abstract class AbsPlayerController extends FrameLayout implements View.OnTouchListener{

    private Context mContext;
    public BaseVideoPlayer mVideoPlayer;
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    public AbsPlayerController(@NonNull Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
    }

    public void setVideoPlayer(BaseVideoPlayer videoPlayer) {
        mVideoPlayer = videoPlayer;
    }

    /**
     * 设置不操作后，多久自动隐藏头部和底部布局
     * @param time
     */
    public abstract void setHideTime(long time);

    /**
     * 设置视频的标题
     * @param title
     */
    public abstract void setTitle(String title);

    /**
     * 设置视频底图资源
     * @param resId
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageView控件，提供给外部用图片工具来
     * 加载网络图片
     * @return
     */
    public abstract ImageView imageView();

    public abstract void setLength(long length);

    public abstract void setLength(String length);

    /**
     * 当播放器的播放状态发生变化，在此方法中更新不同的播放状态的UI
     *
     * @param playState 播放状态：
     *                  STATE_IDLE
     *                  STATE_PREPARING
     *                  STATE_PREPARED
     *                  STATE_PLAYING
     *                  STATE_PAUSED
     *                  STATE_BUFFERING_PLAYING
     *                  STATE_BUFFERING_PAUSED
     *                  STATE_ERROR
     *                  STATE_COMPLETED
     */
    public abstract void onPlayStateChanged(int playState);


    /**
     * 当播放器的播放模式发生变化，在此方法中更新不同模式下的控制器界面。
     *
     * @param playMode 播放器的模式：
     *                 MODE_NORMAL
     *                 MODE_FULL_SCREEN
     *                 MODE_TINY_WINDOW
     */
    public abstract void onPlayModeChanged(int playMode);

    /**
     * 重置控制器，将控制器恢复到初始状态。
     */
    public abstract void reset();

    /**
     * 开启更新进度的计时器。
     */
    public void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();

        // Java并发，Timer的缺陷，用ScheduledExecutorService替代
        /*if(pool==null){
            pool = Executors.newScheduledThreadPool(1);
            //pool = Executors.newSingleThreadScheduledExecutor();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    AbsVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        pool.scheduleWithFixedDelay(mUpdateProgressTimerTask,0,1000, TimeUnit.MILLISECONDS);*/

        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }

        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    AbsPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);
    }

    /**
     * 取消更新进度的计时器。
     */
    public void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }

        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 更新进度，包括进度条进度，展示的当前播放位置时长，总时长等。
     */
    public abstract void updateProgress();


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return false;
    }

    /**
     * 手势左右滑动改变播放位置时，显示控制器中间的播放位置变化视图，
     * 在手势滑动ACTION_MOVE的过程中，会不断调用此方法。
     *
     * @param duration            视频总时长ms
     * @param newPositionProgress 新的位置进度，取值0到100。
     */
    public abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放位置后，手势up或者cancel时，隐藏控制器中间的播放位置变化视图，
     * 在手势ACTION_UP或ACTION_CANCEL时调用。
     */
    public abstract void hideChangePosition();

    public abstract void showChangeVolume(int newVolumeProgress);

    public abstract void hideChangeVolume();

    public abstract void showChangeBrightness(int newBrightnessProgress);

    public abstract void hideChangeBrightness();
}
