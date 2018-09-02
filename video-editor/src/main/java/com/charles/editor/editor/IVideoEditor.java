package com.charles.editor.editor;

import android.graphics.Bitmap;
import android.graphics.Rect;

import com.charles.editor.entry.VideoInfo;
import com.charles.editor.utils.VideoEditConstants;

/**
 * Created by charles on 2018/9/2.
 */

public interface IVideoEditor {
    VideoInfo getTXVideoInfo();

    void setTXVideoPreviewListener(VideoPreviewListener mPreviewListener);

    void setCutFromTime(long startTime, long endTime);

    void initWithPreview(VideoEditConstants.PreviewParam param);

    void previewAtTime(long timeMs);

    void startPlayFromTime(long startTime, long endTime);

    void resumePlay();

    void pausePlay();

    void stopPlay();

    void setVideoGenerateListener(VideoGenerateListener listener);

    void release();

    void setTailWaterMark(Bitmap tailWaterMarkBitmap, VideoEditConstants.TXRect txRect, int i);

    void cancel();

    //mVideoOutputPath not used!
    void generateVideo(int videoCompressed720p, String mVideoOutputPath);

    void setThumbnailListener(ThumbnailListener mThumbnailListener);

    void processVideo();

    void setCutRegion(Rect clipRect);

    void setVideoRegion(Rect rect);

    interface ThumbnailListener {
        void onThumbnail(int index, long thumbAtMs, Bitmap bitmap);
    }

    interface VideoReverseListener {
        void onReverseComplete(VideoEditConstants.GenerateResult var1);
    }

    interface VideoProcessListener {
        void onProcessProgress(float progress, Bitmap bitmap);

        void onProcessComplete(VideoEditConstants.GenerateResult var1);
    }

    interface VideoPreviewListener {

        void onVideoReady(int width, int height);

        void onPreviewProgress(int var1);

        void onPreviewFinished();
    }

    interface VideoGenerateListener {
        void onGenerateProgress(float var1);

        void onGenerateComplete(VideoEditConstants.GenerateResult var1);
    }
}
