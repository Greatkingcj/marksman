package com.charles.ijkplayer.controller;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.huya.ijkplayer.R;
import com.charles.ijkplayer.listeners.OnVideoBackListener;
import com.charles.ijkplayer.utils.VideoPlayerUtils;
import com.charles.ijkplayer.view.videoview.BaseVideoPlayer;
import com.charles.ijkplayer.view.videoview.HuyaVideoView;

/**
 * Created by charles on 2018/3/13.
 * 播放界面的控制, 例如播放/暂停，全屏等
 * 如果需要定制播放界面可以通过实现AbsPlayerController来实现
 * 通过HuyaVideoView获取相应的播放状态以及回调
 */

public class HuyaPlayerController extends AbsPlayerController implements View.OnClickListener {


    private Context mContext;
    private ImageView mImage;
    private ImageView mCenterStart;
    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private TextView mClarity;
    private ImageView mFullScreen;
    private LinearLayout mLoading;
    private ProgressBar pbLoadingRing;
    private ProgressBar pbLoadingQq;
    private TextView mLoadText;
    private LinearLayout mChangePosition;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;
    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;
    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;
    private LinearLayout mError;
    private TextView mRetry;
    private LinearLayout mCompleted;
    private TextView mReplay;
    private TextView mShare;
    private FrameLayout mFlLock;
    private ImageView mIvLock;

    private boolean topBottomVisible;
    private CountDownTimer mDismissTopBottomCountDownTime;
    /**
     * 不操作界面，time时间之后，则自动隐藏顶部和底部视图布局
     */
    private long time;

    public HuyaPlayerController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.custom_video_player, this, true);
        initView();
        initListener();
    }

    private void initView() {
        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mImage = (ImageView) findViewById(R.id.image);
        mTop = (LinearLayout) findViewById(R.id.top);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);

        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mRestartPause = (ImageView) findViewById(R.id.restart_or_pause);
        mPosition = (TextView) findViewById(R.id.position);
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mFullScreen = (ImageView) findViewById(R.id.full_screen);
        mClarity = (TextView) findViewById(R.id.clarity);
        mLoading = (LinearLayout) findViewById(R.id.loading);
        pbLoadingRing = (ProgressBar)findViewById(R.id.pb_loading_ring);
        pbLoadingQq = (ProgressBar)findViewById(R.id.pb_loading_qq);

        mLoadText = (TextView) findViewById(R.id.load_text);
        mChangePosition = (LinearLayout) findViewById(R.id.change_position);
        mChangePositionCurrent = (TextView) findViewById(R.id.change_position_current);
        mChangePositionProgress = (ProgressBar) findViewById(R.id.change_position_progress);
        mChangeBrightness = (LinearLayout) findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = (ProgressBar) findViewById(R.id.change_brightness_progress);
        mChangeVolume = (LinearLayout) findViewById(R.id.change_volume);
        mChangeVolumeProgress = (ProgressBar) findViewById(R.id.change_volume_progress);

        mError = (LinearLayout) findViewById(R.id.error);
        mRetry = (TextView) findViewById(R.id.retry);
        mCompleted = (LinearLayout) findViewById(R.id.completed);
        mReplay = (TextView) findViewById(R.id.replay);
        mShare = (TextView) findViewById(R.id.share);
        mFlLock = (FrameLayout) findViewById(R.id.fl_lock);
        mIvLock = (ImageView) findViewById(R.id.iv_lock);
    }

    private void initListener() {
        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mFlLock.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mVideoPlayer.isBufferingPaused() || mVideoPlayer.isPaused()) {
                    //mVideoPlayer.restart();
                }
                long position = (long) (mVideoPlayer.getDuration() * seekBar.getProgress() / 100f);
                mVideoPlayer.seekTo(position);
                startDismissTopBottomTimer();
            }
        });
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            if (mVideoPlayer.isPrepared() || mVideoPlayer.isPaused()) {
                mVideoPlayer.start();
            }
            mCenterStart.setVisibility(GONE);
        } else if (v == mBack) {
            if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isNormal()) {
                if (mBackListener != null) {
                    mBackListener.onBackClick();
                }
            }
        } else if (v == mRestartPause) {
            if (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying()) {
                mVideoPlayer.pause();
            } else if (mVideoPlayer.isPrepared() || mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused()) {
                mVideoPlayer.restart();
            }
            mCenterStart.setVisibility(GONE);
        } else if (v == mFullScreen) {
            if (mVideoPlayer.isNormal()) {
                mVideoPlayer.enterFullScreen();
            } else if (mVideoPlayer.isFullScreen()) {
                mVideoPlayer.exitFullScreen();
            }
        } else if (v == this) {
            if (mVideoPlayer.isPrepared() || mVideoPlayer.isPlaying() || mVideoPlayer.isPaused()
                    || mVideoPlayer.isBufferingPlaying() || mVideoPlayer.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }
        }
    }

    @Override
    public void setHideTime(long time) {
        this.time = time;
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setImage(int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setLength(long length) {

    }

    @Override
    public void setLength(String length) {

    }

    @Override
    public void setVideoPlayer(BaseVideoPlayer videoPlayer) {
        super.setVideoPlayer(videoPlayer);
    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case HuyaVideoView.STATE_IDLE:
                break;
            case HuyaVideoView.STATE_PREPARING:
                mImage.setVisibility(GONE);
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mTop.setVisibility(GONE);
                mBottom.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                break;
            case HuyaVideoView.STATE_PREPARED:
                setTopBottomVisible(true);
                startUpdateProgressTimer();
                break;
            case HuyaVideoView.STATE_PLAYING:
                mLoading.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case HuyaVideoView.STATE_PAUSED:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case HuyaVideoView.STATE_BUFFERING_PLAYING:
                mLoading.setVisibility(VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();
                break;
            case HuyaVideoView.STATE_BUFFERING_PAUSE:
                mLoading.setVisibility(VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();
                break;
            case HuyaVideoView.STATE_ERROR:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(VISIBLE);
                mError.setVisibility(VISIBLE);
                break;
            case HuyaVideoView.STATE_COMPLETED:
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(VISIBLE);
                mCompleted.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case HuyaVideoView.MODE_NORMAL:
                mFlLock.setVisibility(GONE);
                mBack.setVisibility(VISIBLE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                mFullScreen.setVisibility(VISIBLE);
                break;
            case HuyaVideoView.MODE_FULL_SCREEN:
                mFlLock.setVisibility(VISIBLE);
                mBack.setVisibility(VISIBLE);
                mFullScreen.setVisibility(GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                break;
            default:
                break;
        }
    }

    @Override
    public void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);
        mFlLock.setVisibility(GONE);
        mImage.setVisibility(VISIBLE);
        mBottom.setVisibility(GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
        mTop.setVisibility(VISIBLE);
        mBack.setVisibility(VISIBLE);

        mLoading.setVisibility(GONE);
        mError.setVisibility(GONE);
        mCompleted.setVisibility(GONE);
    }

    @Override
    public void updateProgress() {
        long position = mVideoPlayer.getCurrentPosition();
        long duration = mVideoPlayer.getDuration();
        int bufferPercentage = mVideoPlayer.getBufferPercentage();
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(VideoPlayerUtils.formatTime(position));
        mDuration.setText(VideoPlayerUtils.formatTime(duration));
    }

    @Override
    public void showChangePosition(long duration, int newPositionProgress) {
        mChangePosition.setVisibility(VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(VideoPlayerUtils.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(VideoPlayerUtils.formatTime(newPosition));
    }

    @Override
    public void hideChangePosition() {
        mChangePosition.setVisibility(GONE);
    }

    @Override
    public void showChangeVolume(int newVolumeProgress) {

    }

    @Override
    public void hideChangeVolume() {

    }

    @Override
    public void showChangeBrightness(int newBrightnessProgress) {

    }

    @Override
    public void hideChangeBrightness() {

    }

    /**
     * 开启top,bottom自动消失的timer
     * 比如视频常用功能，当用户5秒不操作后，自动隐藏头部和顶部
     */
    private void startDismissTopBottomTimer() {
        if (time == 0) {
            time = 8000;
        }

        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTime == null) {
            mDismissTopBottomCountDownTime = new CountDownTimer(time, time) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTime.start();

    }

    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTime != null) {
            mDismissTopBottomCountDownTime.cancel();
        }
    }

    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? VISIBLE : GONE);
        mBottom.setVisibility(visible ? VISIBLE : GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mVideoPlayer.isPaused() && !mVideoPlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }
    }

    private OnVideoBackListener mBackListener;
    public void setOnVideoVackListener(OnVideoBackListener listener) {
        this.mBackListener = listener;
    }

}
