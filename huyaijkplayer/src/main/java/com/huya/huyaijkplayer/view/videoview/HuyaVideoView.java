package com.huya.huyaijkplayer.view.videoview;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.huya.huyaijkplayer.controller.AbsPlayerController;
import com.huya.huyaijkplayer.manager.AndroidVideoCacheManager;
import com.huya.huyaijkplayer.manager.VideoPlayerManager;
import com.huya.huyaijkplayer.media.FileMediaDataSource;
import com.huya.huyaijkplayer.utils.VideoPlayerUtils;
import com.huya.huyaijkplayer.view.renderview.IRenderView;
import com.huya.huyaijkplayer.view.renderview.SurfaceRenderView;
import com.huya.huyaijkplayer.view.renderview.TextureRenderView;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by charles on 2018/3/12.
 * 播放器view, 使用了ijkplayer来控制播放，并对外提供播放状态
 */

public class HuyaVideoView extends FrameLayout implements BaseVideoPlayer {

    private static final String TAG = "HuyaVideoView";
    private Uri mUri;
    private String mUrl;
    private Map<String, String> mHeaders;

    // all possible internal states
    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_BUFFERING_PLAYING = 5;
    public static final int STATE_BUFFERING_PAUSE = 6;
    public static final int STATE_COMPLETED = 7;

    //callback listeners
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;

    //window mode
    public static final int MODE_NORMAL = 10;
    public static final int MODE_FULL_SCREEN = 11;
    public static final int MODE_TINY_WINDOW = 12;

    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private int mCurrentMode = MODE_NORMAL;

    //缓存服务器
    private HttpProxyCacheServer proxy;

    //about renderview
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;

    //Sar - sample aspect radio 采样纵横比，表示横向的像素点数和纵向的像素点数的比值。
    //num - numerator 分子
    //den - denominator 分母
    private int mVideoSarNum;
    private int mVideoSarDen;
    private IRenderView mRenderView;
    private IRenderView.ISurfaceHolder mSurfaceHolder = null;

    /**
     * IjkPlayer，基于IjkPlayer封装播放器
     **/
    public static final int TYPE_IJK = 101;

    /**
     * 播放类型
     * TYPE_IJK             基于IjkPlayer封装播放器
     * TYPE_NATIVE          基于原生自带的播放器控件
     **/
    private int mPlayerType = TYPE_IJK;

    public static final int RENDER_NONE = 1001;
    public static final int RENDER_SURFACE_VIEW = 1002;
    public static final int RENDER_TEXTURE_VIEW = 1003;

    private int mRenderType = RENDER_TEXTURE_VIEW;

    private Context mContext;
    private FrameLayout mContainer;
    private AudioManager mAudioManger;
    private IMediaPlayer mMediaPlayer;
    private AbsPlayerController mController;
    private int mBufferPercentage;
    private boolean continueFromLastPosition;
    private long skipToPosition;

    public HuyaVideoView(@NonNull Context context) {
        this(context, null);
    }

    public HuyaVideoView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
        initRenders();
    }

    @Override
    public void setUp(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

    /**
     * Sets video path.
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        //开启缓存
        mUrl = path;
        proxy = AndroidVideoCacheManager.getProxy(mContext);
        proxy.registerCacheListener(mCacheListener, path);
        String proxyUrl = proxy.getProxyUrl(path);
        setVideoURI(Uri.parse(proxyUrl));
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    /**
     * Sets video URI using specific headers.
     *
     * @param uri     the URI of the video.
     * @param headers the headers for the URI request.
     *                Note that the cross domain redirection is allowed by default, but that can be
     *                changed with key/value pairs through the headers parameter with
     *                "android-allow-cross-domain-redirect" as the key and "0" or "1" as the value
     *                to disallow or allow cross domain redirection.
     */
    private void setVideoURI(Uri uri, Map<String, String> headers) {
        mUri = uri;
        mHeaders = headers;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setController(AbsPlayerController controller) {
        mContainer.removeView(mController);
        mController = controller;
        mController.reset();
        mController.setVideoPlayer(this);
        ViewGroup.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContainer.addView(mController, params);

    }

    public void setPlayerType(int playerType) {
        mPlayerType = playerType;
    }

    public void setRenderType(int renderType) {
        mRenderType = renderType;
    }

    private static final int[] s_allAspectRatio = {
            IRenderView.AR_ASPECT_FIT_PARENT,
            IRenderView.AR_ASPECT_FILL_PARENT,
            IRenderView.AR_ASPECT_WRAP_CONTENT,
            IRenderView.AR_MATCH_PARENT,
            IRenderView.AR_16_9_FIT_PARENT,
            IRenderView.AR_4_3_FIT_PARENT};
    private int mCurrentAspectRatioIndex = 0;
    private int mCurrentAspectRatio = s_allAspectRatio[0];

    private void initRenders() {

        switch (mRenderType) {
            case RENDER_NONE:
                setRenderView(null);
                break;
            case RENDER_TEXTURE_VIEW: {
                TextureRenderView renderView = new TextureRenderView(getContext());
                if (mMediaPlayer != null) {
                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                    renderView.setAspectRatio(mCurrentAspectRatio);
                }
                setRenderView(renderView);
                break;
            }
            case RENDER_SURFACE_VIEW: {
                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
                setRenderView(renderView);
                break;
            }
            default:
                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", mRenderType));
                break;
        }
    }

    public void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null)
                mMediaPlayer.setDisplay(null);

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView = null;
            removeView(renderUIView);
        }

        if (renderView == null)
            return;

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0)
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        if (mVideoSarNum > 0 && mVideoSarDen > 0)
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);

        View renderUIView = mRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        mContainer.addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }
        }

        @Override
        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
            else
                openVideo();
        }

        @Override
        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }

            // after we return from this we can't use the surface any more
            mSurfaceHolder = null;
            // REMOVED: if (mMediaController != null) mMediaController.hide();
            // REMOVED: release(true);
            releaseWithoutStop();
        }
    };

    public void releaseWithoutStop() {
        if (mMediaPlayer != null)
            mMediaPlayer.setDisplay(null);
    }

    /**
     * 初始化音频管理器
     */
    private void initAudioManager() {
        if (mAudioManger == null) {
            mAudioManger = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        mAudioManger.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    /**
     * 初始化MediaPlayer
     */
    private void initMediaPlayer() {
        switch (mPlayerType) {
            //TODO:通过Settings增加其它播放器的支持
            case TYPE_IJK:
            default: {
                IjkMediaPlayer ijkMediaPlayer = null;
                ijkMediaPlayer = new IjkMediaPlayer();
                /*ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
                 //设置软硬解码 ijk是基于FFmpeg的，0采用软解，1采用硬解
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
                //google mp4旋转角度变化时通知
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
                //video size changed会通知
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
                //framedrop 是在视频帧处理不过来的时候丢弃一些帧达到同步的效果。
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
                //根据选项值选择的帧类型进行解码器丢弃处理。
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);*/
                //调用prepareAsync是不直接播放，而是到准备状态
                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
                mMediaPlayer = ijkMediaPlayer;
            }
            break;
        }

        try {
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
            mBufferPercentage = 0;
            String scheme = mUri.getScheme();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (TextUtils.isEmpty(scheme) || scheme.equalsIgnoreCase("file"))) {
                IMediaDataSource dataSource = new FileMediaDataSource(new File(mUri.toString()));
                mMediaPlayer.setDataSource(dataSource);
            }  else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mMediaPlayer.setDataSource(mContext, mUri, mHeaders);
            } else {
                mMediaPlayer.setDataSource(mUri.toString());
            }
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();

            // we don't set the target state here either, but preserve the
            // target state that was there before.
            mCurrentState = STATE_PREPARING;
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            // not ready for playback just yet, will try again later
            return;
        }

        //initAudioManager
        initAudioManager();

        // initMediaPlayer
        initMediaPlayer();


    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            VideoPlayerManager.getInstance().setCurrentVideoPlayer(this);
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        } else {
            //only in idle state can start
        }
        mTargetState = STATE_PLAYING;
    }

    @Override
    public void start(long position) {
        skipToPosition = position;
        start();
    }

    @Override
    public void restart() {
        if (mCurrentState == STATE_PREPARED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
        } else if (mCurrentState == STATE_PAUSED) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
        } else if (mCurrentState == STATE_BUFFERING_PAUSE) {
            mMediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PAUSE;
            mController.onPlayStateChanged(mCurrentState);
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mMediaPlayer.reset();
        } else {
            //can not call the method
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
        }

        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mMediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSE;
            mController.onPlayStateChanged(mCurrentState);
        }
    }

    @Override
    public void seekTo(long pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public void setVolume(int volume) {
        if (mAudioManger != null && isInPlaybackState()) {
            mAudioManger.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }
    }

    @Override
    public void setSpeed(float speed) {
        if (mMediaPlayer instanceof IjkMediaPlayer) {
            ((IjkMediaPlayer)mMediaPlayer).setSpeed(speed);
        } else {
            //other player can do nothing
        }
    }

    @Override
    public void continueFromLastPosition(boolean continueFromLastPosition) {
        this.continueFromLastPosition = continueFromLastPosition;
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSE;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mCurrentMode == MODE_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return false;
    }

    @Override
    public boolean isNormal() {
        return mCurrentMode == MODE_NORMAL;
    }

    @Override
    public int getMaxVolume() {
        if (mAudioManger != null) {
            return mAudioManger.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (mAudioManger != null) {
            return mAudioManger.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }

        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public float getSpeed(float speed) {
        if (isInPlaybackState() && mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getSpeed(speed);
        }
        return 0;
    }

    @Override
    public long getTcpSpeed() {
        if (isInPlaybackState() && mMediaPlayer instanceof IjkMediaPlayer) {
            return ((IjkMediaPlayer) mMediaPlayer).getTcpSpeed();
        }
        return 0;
    }

    /**
     * 进入全屏模式
     * 全屏，将mContainer(内部包含mTextureView和mController)从当前容器中移除，并添加到android.R.content中.
     * 切换横屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期
     */
    @Override
    public void enterFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN){
            return;
        }
        // 隐藏ActionBar、状态栏，并横屏
        VideoPlayerUtils.hideActionBar(mContext);
        VideoPlayerUtils.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup contentView = (ViewGroup) VideoPlayerUtils.scanForActivity(mContext).findViewById(android.R.id.content);
        if (mCurrentMode == MODE_TINY_WINDOW) {
            contentView.removeView(mContainer);
        } else {
            this.removeView(mContainer);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);

        mCurrentMode = MODE_FULL_SCREEN;
        mController.onPlayModeChanged(mCurrentMode);
    }

    /**
     * 退出全屏模式
     * 退出全屏，移除mTextureView和mController，并添加到非全屏的容器中。
     * 切换竖屏时需要在manifest的activity标签下添加android:configChanges="orientation|keyboardHidden|screenSize"配置，
     * 以避免Activity重新走生命周期.
     *
     * @return true退出全屏.
     */
    @Override
    public boolean exitFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) {
            VideoPlayerUtils.showActionBar(mContext);
            VideoPlayerUtils.scanForActivity(mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup contentView = (ViewGroup) VideoPlayerUtils.scanForActivity(mContext).findViewById(android.R.id.content);
            //将视图移除
            contentView.removeView(mContainer);
            //重新添加到当前视图
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);
            mCurrentMode = MODE_NORMAL;
            mController.onPlayModeChanged(mCurrentMode);
            return true;
        }
        return false;
    }

    @Override
    public void enterTinyWindow() {

    }

    @Override
    public boolean exitTinyWindow() {
        return false;
    }

    @Override
    public void releasePlayer() {
        if (mAudioManger != null) {
            //放弃音频焦点。使以前的焦点所有者(如果有的话)接收焦点。
            mAudioManger.abandonAudioFocus(null);
            //置空
            mAudioManger = null;
        }
        if (mMediaPlayer != null) {
            //释放视频焦点
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mCurrentState = STATE_IDLE;
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (mAudioManger != null) {
                mAudioManger.abandonAudioFocus(null);
                mMediaPlayer = null;
            }
            mCurrentState = STATE_IDLE;
        }
    }

    CacheListener mCacheListener = new CacheListener() {
        @Override
        public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
            Log.e(TAG, "缓冲到了percentsAvailable: " + percentsAvailable);
            mBufferPercentage = percentsAvailable;
        }
    };

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                        requestLayout();
                    }
                }
            };

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            mController.onPlayStateChanged(mCurrentState);
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
            if (continueFromLastPosition) {
                long savedPlayPosition = VideoPlayerUtils.getSavedPlayPosition(mContext, mUrl);
                mp.seekTo(savedPlayPosition);
            }

            // 跳到指定位置播放
            if (skipToPosition != 0) {
                mp.seekTo(skipToPosition);
            }

        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    mCurrentState = STATE_COMPLETED;
                    mTargetState = STATE_COMPLETED;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                    mController.onPlayStateChanged(mCurrentState);
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int what, int extra) {
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, what, extra);
                    }
                    /*if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        // 播放器开始渲染
                        mCurrentState = STATE_PLAYING;
                        mController.onPlayStateChanged(mCurrentState);
                    } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START) {
                        // MediaPlayer暂时不播放，以缓冲更多的数据
                        if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSE) {
                            mCurrentState = STATE_BUFFERING_PAUSE;
                        } else {
                            mCurrentState = STATE_BUFFERING_PLAYING;
                        }
                        mController.onPlayStateChanged(mCurrentState);
                    } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END) {
                        // 填充缓冲区后，MediaPlayer恢复播放/暂停
                        if (mCurrentState == STATE_BUFFERING_PLAYING) {
                            mCurrentState = STATE_PLAYING;
                            mController.onPlayStateChanged(mCurrentState);
                        }
                        if (mCurrentState == STATE_BUFFERING_PAUSE) {
                            mCurrentState = STATE_PAUSED;
                            mController.onPlayStateChanged(mCurrentState);
                        }
                    } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED) {

                    } else if (what == IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE) {

                    } else {

                    }*/
                    return true;
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "Error: " + framework_err + "," + impl_err);
                    mCurrentState = STATE_ERROR;
                    mTargetState = STATE_ERROR;
                    /* If an error handler has been supplied, use it and finish. */
                    if (mOnErrorListener != null) {
                       mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err);
                    }
                    mController.onPlayStateChanged(mCurrentState);
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                    //mBufferPercentage = percent;
                }
            };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            proxy.unregisterCacheListener(mCacheListener, mUrl);
        }
    };

    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer mp, IjkTimedText text) {

        }
    };

    /**
     * Register a callback to be invoked when the media file
     * is loaded and ready to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file
     * has been reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs
     * during playback or setup.  If no listener is specified,
     * or if the listener returned false, VideoView will inform
     * the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * Register a callback to be invoked when an informational event
     * occurs during playback or setup.
     *
     * @param l The callback that will be run
     */
    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }
}
