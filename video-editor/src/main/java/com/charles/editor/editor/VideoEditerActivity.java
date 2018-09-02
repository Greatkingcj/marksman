package com.charles.editor.editor;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.charles.editor.R;
import com.charles.editor.common.TCToolsView;
import com.charles.editor.common.widget.VideoWorkProgressFragment;
import com.charles.editor.common.widget.videotimeline.VideoProgressController;
import com.charles.editor.common.widget.videotimeline.VideoProgressView;
import com.charles.editor.cutter.TCCutterFragment;
import com.charles.editor.preview.VideoPreviewActivity;
import com.charles.editor.utils.CommonConstants;
import com.charles.editor.utils.EditerUtil;
import com.charles.editor.utils.PlayState;
import com.charles.editor.utils.VideoEditConstants;
import com.charles.editor.utils.VideoRecordCommon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


/**
 * Created by hans on 2017/11/6.
 */

public class VideoEditerActivity extends FragmentActivity implements
        View.OnClickListener,
        VideoEditerWrapper.TXVideoPreviewListenerWrapper,
        IVideoEditor.VideoGenerateListener {
    private static final String TAG = "VideoEditerActivity";

    // 短视频SDK获取到的视频信息
    private IVideoEditor mTXVideoEditer;                   // SDK接口类
    /**
     * 布局相关
     */
    private LinearLayout mLlBack;                           // 左上角返回
    private FrameLayout mVideoPlayerLayout;                 // 视频承载布局
    private ImageButton mIbPlay;                            // 播放按钮
    private TextView mTvDone;
    private TCToolsView mToolsView;                         // 底部工具栏

    private VideoWorkProgressFragment mWorkLoadingProgress; // 生成视频的等待框


    private Fragment mCurrentFragment,                      // 标记当前的Fragment
            mCutterFragment,                                // 裁剪的Fragment
            mTimeFragment,                                  // 时间特效的Fragment
            mStaticFilterFragment,                          // 静态滤镜的Fragment
            mMotionFragment,                                // 动态滤镜的Fragment
            mBGMSettingFragment;                            // BGM设置的Fragment

    private int mCurrentState = PlayState.STATE_NONE;       // 播放器当前状态

    private String mVideoOutputPath;                        // 视频输出路径
    private int mVideoResolution = -1;                      // 分辨率类型（如果是从录制过来的话才会有，这参数）

    private long mVideoDuration;                            // 视频的总时长
    private long mPreviewAtTime;                            // 当前单帧预览的时间

    private TXPhoneStateListener mPhoneListener;            // 电话监听

    private KeyguardManager mKeyguardManager;
    private int mVideoFrom;

    /**
     * 缩略图进度条相关
     */
    private VideoProgressView mVideoProgressView;
    private VideoProgressController mVideoProgressController;
    private VideoProgressController.VideoProgressSeekListener mVideoProgressSeekListener = new VideoProgressController.VideoProgressSeekListener() {
        @Override
        public void onVideoProgressSeek(long currentTimeMs) {
            Log.i(TAG, "onVideoProgressSeek, currentTimeMs = " + currentTimeMs);

            previewAtTime(currentTimeMs);
        }

        @Override
        public void onVideoProgressSeekFinish(long currentTimeMs) {
            Log.i(TAG, "onVideoProgressSeekFinish, currentTimeMs = " + currentTimeMs);

            previewAtTime(currentTimeMs);
        }
    };
    private String mRecordProcessedPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_editer2);
        VideoEditerWrapper wrapper = VideoEditerWrapper.getInstance();
        wrapper.addTXVideoPreviewListenerWrapper(this);

        mTXVideoEditer = wrapper.getEditer();
        if (mTXVideoEditer == null || wrapper.getTXVideoInfo() == null) {
            Toast.makeText(this, "状态异常，结束编辑", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mVideoDuration = mTXVideoEditer.getTXVideoInfo().duration;
        VideoEditerWrapper.getInstance().setCutterStartTime(0, mVideoDuration);


        mVideoResolution = getIntent().getIntExtra(CommonConstants.VIDEO_RECORD_RESOLUTION, -1);

        mVideoFrom = getIntent().getIntExtra(CommonConstants.VIDEO_RECORD_TYPE,CommonConstants.VIDEO_RECORD_TYPE_EDIT);
        // 录制经过预处理的视频路径，在编辑后需要删掉录制源文件
        mRecordProcessedPath = getIntent().getStringExtra(CommonConstants.VIDEO_EDITER_PATH);

        initViews();
        initPhoneListener();
        initVideoProgressLayout();
        previewVideo();// 开始预览视频
        mKeyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
    }

    private void initPhoneListener() {
        //设置电话监听
        if (mPhoneListener == null) {
            mPhoneListener = new TXPhoneStateListener(this);
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }


    private void initViews() {
        mLlBack = (LinearLayout) findViewById(R.id.editer_back_ll);
        mLlBack.setOnClickListener(this);
        mTvDone = (TextView) findViewById(R.id.editer_tv_done);
        mTvDone.setOnClickListener(this);
        mVideoPlayerLayout = (FrameLayout) findViewById(R.id.editer_fl_video);

        mIbPlay = (ImageButton) findViewById(R.id.editer_ib_play);
        mIbPlay.setOnClickListener(this);
    }


    private long getCutterStartTime() {
        return mCutterFragment != null ? ((TCCutterFragment) mCutterFragment).getCutterStartTime() : 0;
    }

    private long getCutterEndTime() {
        return mCutterFragment != null ? ((TCCutterFragment) mCutterFragment).getCutterEndTime() : 0;
    }

    /**
     * ==========================================SDK播放器生命周期==========================================
     */

    private void previewVideo() {
        showCutterFragment();
        initVideoProgressLayout();  // 初始化进度布局
        initPlayerLayout();         // 初始化预览视频布局
        startPlay(getCutterStartTime(), getCutterEndTime());  // 开始播放
    }


    private void initVideoProgressLayout() {
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int screenWidth = point.x;
        mVideoProgressView = (VideoProgressView) findViewById(R.id.editer_video_progress_view);
        mVideoProgressView.setViewWidth(screenWidth);

        List<Bitmap> thumbnailList = VideoEditerWrapper.getInstance().getAllThumbnails();
        mVideoProgressView.setThumbnailData(thumbnailList);

        mVideoProgressController = new VideoProgressController(mVideoDuration);
        mVideoProgressController.setVideoProgressView(mVideoProgressView);
        mVideoProgressController.setVideoProgressSeekListener(mVideoProgressSeekListener);
        mVideoProgressController.setVideoProgressDisplayWidth(screenWidth);

    }

    public void switchReverse() {
        mVideoProgressView.setReverse();
    }

    private void initPlayerLayout() {
        VideoEditConstants.PreviewParam param = new VideoEditConstants.PreviewParam();
        param.videoView = mVideoPlayerLayout;
        param.renderMode = VideoEditConstants.PREVIEW_RENDER_MODE_FILL_EDGE;
        mTXVideoEditer.initWithPreview(param);
    }

    /**
     * 调用mTXVideoEditer.previewAtTime后，需要记录当前时间，下次播放时从当前时间开始
     * x
     *
     * @param timeMs
     */
    public void previewAtTime(long timeMs) {
        pausePlay();
        mTXVideoEditer.previewAtTime(timeMs);
        mPreviewAtTime = timeMs;
        mCurrentState = PlayState.STATE_PREVIEW_AT_TIME;
    }

    /**
     * 给子Fragment调用 （子Fragment不在意Activity中对于播放器的生命周期）
     */
    public void startPlayAccordingState(long startTime, long endTime) {
        if (mCurrentState == PlayState.STATE_STOP || mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_PREVIEW_AT_TIME) {
            startPlay(startTime, endTime);
        } else if (mCurrentState == PlayState.STATE_PAUSE) {
            resumePlay();
        }
    }

    /**
     * 给子Fragment调用 （子Fragment不在意Activity中对于播放器的生命周期）
     */
    public void restartPlay() {
        stopPlay();
        startPlay(getCutterStartTime(), getCutterEndTime());
    }

    public void startPlay(long startTime, long endTime) {
        mTXVideoEditer.startPlayFromTime(startTime, endTime);
        mCurrentState = PlayState.STATE_PLAY;
        mIbPlay.setImageResource(R.drawable.ic_pause);
    }


    public void resumePlay() {
        if (mCurrentState == PlayState.STATE_PAUSE) {
            mTXVideoEditer.resumePlay();
            mCurrentState = PlayState.STATE_RESUME;
            mIbPlay.setImageResource(R.drawable.ic_pause);

        }
    }

    public void pausePlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            mTXVideoEditer.pausePlay();
            mCurrentState = PlayState.STATE_PAUSE;
            mIbPlay.setImageResource(R.drawable.ic_play);
        }
    }

    public void stopPlay() {
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY ||
                mCurrentState == PlayState.STATE_STOP || mCurrentState == PlayState.STATE_PAUSE) {
            mTXVideoEditer.stopPlay();
            mCurrentState = PlayState.STATE_STOP;
            mIbPlay.setImageResource(R.drawable.ic_play);
        }
    }


    /**
     * ==========================================activity生命周期==========================================
     */

    @Override
    protected void onRestart() {
        super.onRestart();
        // 在oppo r9s上，锁屏后，按电源键进入解锁状态（屏保画面），也会走onRestart和onResume。因此做个保护
        if( !mKeyguardManager.inKeyguardRestrictedInputMode() ){
            initPlayerLayout();
//            startPlayAccordingState(getCutterStartTime(), getCutterEndTime());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if( !mKeyguardManager.inKeyguardRestrictedInputMode() ){
            restartPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlay();
        // 若当前处于生成状态，离开当前activity，直接停止生成
        if (mCurrentState == PlayState.STATE_GENERATE) {
            stopGenerate();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPhoneListener != null) {
            TelephonyManager tm = (TelephonyManager) this.getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(mPhoneListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mTXVideoEditer != null) {
            stopPlay();
            mTXVideoEditer.setVideoGenerateListener(null);
            mTXVideoEditer.release();
        }
        // 清除对TXVideoEditer的引用以及相关配置
        VideoEditerWrapper.getInstance().removeTXVideoPreviewListenerWrapper(this);
        VideoEditerWrapper.getInstance().clear();

    }


    /**
     * ==========================================SDK回调==========================================
     */
    @Override // 预览进度回调
    public void onPreviewProgressWrapper(int timeMs) {
        // 视频的进度回调是异步的，如果不是处于播放状态，那么无需修改进度
        if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
            mVideoProgressController.setCurrentTimeMs(timeMs);
        }
    }

    @Override // 预览完成回调
    public void onPreviewFinishedWrapper() {
        Log.d(TAG, "---------------onPreviewFinished-----------------");
        stopPlay();
        if ((mMotionFragment != null && mMotionFragment.isAdded() && !mMotionFragment.isHidden()) ||
                (mTimeFragment != null && mTimeFragment.isAdded() && !mTimeFragment.isHidden())) {
            // 处于动态滤镜或者时间特效界面,忽略 不做任何操作
        } else {
            // 如果当前不是动态滤镜界面或者时间特效界面，那么会自动开始重复播放
            startPlay(getCutterStartTime(), getCutterEndTime());
        }
    }


    /**
     * 创建缩略图，并跳转至视频预览的Activity
     */
    private void createThumbFile(final VideoEditConstants.GenerateResult result) {
       /* AsyncTask<Void, String, String> task = new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                File outputVideo = new File(mVideoOutputPath);
                if (!outputVideo.exists())
                    return null;
                Bitmap bitmap = TXVideoInfoReader.getInstance().getSampleImage(0, mVideoOutputPath);
                if (bitmap == null)
                    return null;
                String mediaFileName = outputVideo.getAbsolutePath();
                if (mediaFileName.lastIndexOf(".") != -1) {
                    mediaFileName = mediaFileName.substring(0, mediaFileName.lastIndexOf("."));
                }
                String folder = Environment.getExternalStorageDirectory() + File.separator + TCConstants.DEFAULT_MEDIA_PACK_FOLDER + File.separator + mediaFileName;
                File appDir = new File(folder);
                if (!appDir.exists()) {
                    appDir.mkdirs();
                }

                String fileName = "thumbnail" + ".jpg";
                File file = new File(appDir, fileName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return file.getAbsolutePath();
            }

            @Override
            protected void onPostExecute(String s) {
                if (mVideoFrom == CommonConstants.VIDEO_RECORD_TYPE_UGC_RECORD){
                    FileUtils.deleteFile(mRecordProcessedPath);
                }
                startPreviewActivity(result, s);
            }

        };
        task.execute();*/
    }

    private void startPreviewActivity(VideoEditConstants.GenerateResult result, String thumbPath) {
        Intent intent = new Intent(getApplicationContext(), VideoPreviewActivity.class);
        intent.putExtra(CommonConstants.VIDEO_RECORD_TYPE, CommonConstants.VIDEO_RECORD_TYPE_EDIT);
        intent.putExtra(CommonConstants.VIDEO_RECORD_RESULT, result.retCode);
        intent.putExtra(CommonConstants.VIDEO_RECORD_DESCMSG, result.descMsg);
        intent.putExtra(CommonConstants.VIDEO_RECORD_VIDEPATH, mVideoOutputPath);
        if (thumbPath != null)
        {
            intent.putExtra(CommonConstants.VIDEO_RECORD_COVERPATH, thumbPath);
        }
        intent.putExtra(CommonConstants.VIDEO_RECORD_DURATION, getCutterEndTime() - getCutterStartTime());
        startActivity(intent);
        finish();
    }

    /**
     * ==========================================工具栏的点击回调==========================================
     */


    private void showCutterFragment() {
        if (mCutterFragment == null) {
            mCutterFragment = new TCCutterFragment();
        }
        showFragment(mCutterFragment, "cutter_fragment");
    }

    private void showFragment(Fragment fragment, String tag) {
        if (fragment == mCurrentFragment) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        if (!fragment.isAdded()) {
            transaction.add(R.id.editer_fl_container, fragment, tag);
        } else {
            transaction.show(fragment);
        }
        mCurrentFragment = fragment;
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.editer_back_ll) {
            finish();
        } else if (viewId == R.id.editer_tv_done) {
            startGenerateVideo();
        } else if (viewId == R.id.editer_ib_play) {
            Log.i(TAG, "editer_ib_play clicked, mCurrentState = " + mCurrentState);
            if (mCurrentState == PlayState.STATE_NONE || mCurrentState == PlayState.STATE_STOP) {
                startPlay(getCutterStartTime(), getCutterEndTime());
            } else if (mCurrentState == PlayState.STATE_RESUME || mCurrentState == PlayState.STATE_PLAY) {
                pausePlay();
            } else if (mCurrentState == PlayState.STATE_PAUSE) {
                resumePlay();
            } else if (mCurrentState == PlayState.STATE_PREVIEW_AT_TIME) {
                startPlay(mPreviewAtTime, getCutterEndTime());
            }
        }
    }


    /**
     * =========================================视频生成相关==========================================
     */
    private void startGenerateVideo() {
        stopPlay(); // 停止播放

        // 处于生成状态
        mCurrentState = PlayState.STATE_GENERATE;
        // 防止
        mTvDone.setEnabled(false);
        mTvDone.setClickable(false);
        // 生成视频输出路径
        mVideoOutputPath = EditerUtil.generateVideoPath();

        mIbPlay.setImageResource(R.drawable.ic_play);

        if (mWorkLoadingProgress == null) {
            initWorkLoadingProgress();
        }
        mWorkLoadingProgress.setProgress(0);
        mWorkLoadingProgress.setCancelable(false);
        mWorkLoadingProgress.show(getSupportFragmentManager(), "progress_dialog");

        mTXVideoEditer.setCutFromTime(getCutterStartTime(), getCutterEndTime());
        mTXVideoEditer.setVideoGenerateListener(this);

        if (mVideoResolution == -1) {// 默认情况下都将输出720的视频
            mTXVideoEditer.generateVideo(VideoEditConstants.VIDEO_COMPRESSED_720P, mVideoOutputPath);
        } else if (mVideoResolution == VideoRecordCommon.VIDEO_RESOLUTION_360_640) {
            mTXVideoEditer.generateVideo(VideoEditConstants.VIDEO_COMPRESSED_360P, mVideoOutputPath);
        } else if (mVideoResolution == VideoRecordCommon.VIDEO_RESOLUTION_540_960) {
            mTXVideoEditer.generateVideo(VideoEditConstants.VIDEO_COMPRESSED_540P, mVideoOutputPath);
        } else if (mVideoResolution == VideoRecordCommon.VIDEO_RESOLUTION_720_1280) {
            mTXVideoEditer.generateVideo(VideoEditConstants.VIDEO_COMPRESSED_720P, mVideoOutputPath);
        }
    }

    private void stopGenerate() {
        if (mCurrentState == PlayState.STATE_GENERATE) {
            mTvDone.setEnabled(true);
            mTvDone.setClickable(true);
            mWorkLoadingProgress.dismiss();
            Toast.makeText(VideoEditerActivity.this, "取消视频生成", Toast.LENGTH_SHORT).show();
            mWorkLoadingProgress.setProgress(0);
            mCurrentState = PlayState.STATE_NONE;
            if (mTXVideoEditer != null) {
                mTXVideoEditer.cancel();
            }
        }
    }

    @Override // 生成进度回调
    public void onGenerateProgress(float progress) {
        mWorkLoadingProgress.setProgress((int) (progress * 100));
    }

    @Override // 生成完成
    public void onGenerateComplete(VideoEditConstants.GenerateResult result) {
        if (result.retCode == VideoEditConstants.GENERATE_RESULT_OK) {
            // 生成成功
            createThumbFile(result);
        } else {
            Toast.makeText(VideoEditerActivity.this, result.descMsg, Toast.LENGTH_SHORT).show();
        }
        mTvDone.setEnabled(true);
        mTvDone.setClickable(true);
        mCurrentState = PlayState.STATE_NONE;
    }

    /**
     * ==========================================进度条==========================================
     */
    private void initWorkLoadingProgress() {
        if (mWorkLoadingProgress == null) {
            mWorkLoadingProgress = new VideoWorkProgressFragment();
            mWorkLoadingProgress.setOnClickStopListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopGenerate();
                }
            });
        }
        mWorkLoadingProgress.setProgress(0);
    }

    public VideoProgressController getVideoProgressViewController() {
        return mVideoProgressController;
    }


    /*********************************************监听电话状态**************************************************/
    static class TXPhoneStateListener extends PhoneStateListener {
        WeakReference<VideoEditerActivity> mEditer;

        public TXPhoneStateListener(VideoEditerActivity editer) {
            mEditer = new WeakReference<VideoEditerActivity>(editer);
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            VideoEditerActivity activity = mEditer.get();
            if (activity == null) {
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:  //电话等待接听
                case TelephonyManager.CALL_STATE_OFFHOOK:  //电话接听
                    // 生成状态 取消生成
                    if (activity.mCurrentState == PlayState.STATE_GENERATE) {
                        activity.stopGenerate();
                    }
                    // 直接停止播放
                    activity.stopPlay();
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    // 重新开始播放
                    activity.restartPlay();
                    break;
                default:
                    break;
            }
        }
    }
}
