package com.charles.editor.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;

import com.charles.editor.entry.VideoInfo;
import com.charles.editor.utils.VideoEditConstants;

/**
 * Created by charles on 2018/9/2.
 */

public class VideoEditorImpl implements IVideoEditor{

    public VideoEditorImpl(Context context) {

    }

    @Override
    public VideoInfo getTXVideoInfo() {
        return null;
    }

    @Override
    public void setTXVideoPreviewListener(VideoPreviewListener mPreviewListener) {

    }

    @Override
    public void setCutFromTime(long startTime, long endTime) {

    }

    @Override
    public void initWithPreview(VideoEditConstants.PreviewParam param) {

    }

    @Override
    public void previewAtTime(long timeMs) {

    }

    @Override
    public void startPlayFromTime(long startTime, long endTime) {

    }

    @Override
    public void resumePlay() {

    }

    @Override
    public void pausePlay() {

    }

    @Override
    public void stopPlay() {

    }

    @Override
    public void setVideoGenerateListener(VideoGenerateListener listener) {

    }

    @Override
    public void release() {

    }

    @Override
    public void setTailWaterMark(Bitmap tailWaterMarkBitmap, VideoEditConstants.TXRect txRect, int i) {

    }

    @Override
    public void cancel() {

    }

    @Override
    public void generateVideo(int videoCompressed720p, String mVideoOutputPath) {

    }

    @Override
    public void setThumbnailListener(ThumbnailListener mThumbnailListener) {

    }

    @Override
    public void processVideo() {

    }

    @Override
    public void setCutRegion(Rect clipRect) {

    }

    @Override
    public void setVideoRegion(Rect rect) {

    }
}
