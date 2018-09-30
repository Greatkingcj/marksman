package com.charles.editor.preview;

import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.view.Surface;

import com.charles.editor.entry.VideoInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by charles on 2018/9/30.
 */

public class MediaPlayerWrapper implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{

    private MediaPlayer mCurMediaPlayer;
    private List<MediaPlayer> mPlayerList;
    private List<String> mSrcList;
    private List<VideoInfo> mInfoList;
    private Surface surface;
    private IMediaCallback mCallback;
    private int curIndex;

    public MediaPlayerWrapper() {
        mPlayerList = new ArrayList<>();
        mInfoList = new ArrayList<>();
    }

    public void setOnCompletionListener(IMediaCallback callback) {
        this.mCallback = callback;
    }

    public void setDataSource(List<String> dataSource) {
        this.mSrcList = dataSource;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        for (int i = 0; i < dataSource.size(); i++) {
            VideoInfo info = new VideoInfo();
            String path = dataSource.get(i);
            retriever.setDataSource(path);

            String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            String width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            info.videoPath = path;
            info.rotation = Integer.parseInt(rotation);
            info.width = Integer.parseInt(width);
            info.height = Integer.parseInt(height);
            info.duration = Integer.parseInt(duration);

            mInfoList.add(info);
        }
    }

    public List<VideoInfo> getVideoInfo() {
        return mInfoList;
    }

    public void setSurface(Surface surface) {
        this.surface = surface;
    }

    public void prepare() throws IOException{
        for (int i = 0; i < mSrcList.size(); i++) {
            MediaPlayer player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setOnCompletionListener(this);
            player.setOnErrorListener(this);
            player.setOnPreparedListener(this);
            player.setDataSource(mSrcList.get(i));
            player.prepare();
            mPlayerList.add(player);
            if (i == 0) {
                mCurMediaPlayer = player;
                if (mCallback != null) {
                    mCallback.onVideoChanged(mInfoList.get(0));
                }
            }
            if (mCallback != null) {
                mCallback.onVideoPrepare();
            }
        }
    }

    public void start() {
        mCurMediaPlayer.setSurface(surface);
        mCurMediaPlayer.start();
        if (mCallback != null) {
            mCallback.onVideoStart();
        }
    }

    public void pause() {
        mCurMediaPlayer.pause();
        if (mCallback != null) {
            mCallback.onVideoPause();
        }
    }

    public int getCurVideoDuration() {
        return (int)mInfoList.get(curIndex).duration;
    }

    public int getVideoDuration() {
        if (mSrcList.size() == 0) {
            throw new IllegalStateException("please set video src first");
        }

        long duration = 0;
        for (int i = 0; i < mSrcList.size(); i++) {
            duration += mInfoList.get(i).duration;
        }
        return (int) duration;
    }

    public int getCurPosition() {
        int position = 0;
        for (int i = 0; i < curIndex; i++) {
            position += mInfoList.get(i).duration;
        }
        position += mCurMediaPlayer.getCurrentPosition();
        return position;
    }

    public void seekTo(int time) {
        int duration = 0;
        for (int i = 0; i < mInfoList.size(); i++) {
            duration += mInfoList.get(i).duration;
            if (duration > time) {
                int ti = time - (duration - mInfoList.get(i).duration);
                if (curIndex == i) {
                    mCurMediaPlayer.seekTo(ti);
                    if (mCurMediaPlayer.isPlaying()) {
                        pause();
                    }
                } else {
                    curIndex = i;
                    mCurMediaPlayer.setSurface(null);
                    mCurMediaPlayer.seekTo(0);
                    if (mCurMediaPlayer.isPlaying()) {
                        pause();
                    }
                    if (mCallback != null) {
                        mCallback.onVideoChanged(mInfoList.get(i));
                        mCallback.onVideoPause();
                    }
                    mCurMediaPlayer = mPlayerList.get(i);
                    mCurMediaPlayer.setSurface(surface);
                    mCurMediaPlayer.seekTo(ti);
                }
                break;
            }
        }
    }

    public boolean isPlaying() {
        return mCurMediaPlayer.isPlaying();
    }

    public void swithPlayer(MediaPlayer mp) {
        mp.setSurface(null);
        if (mCallback != null) {
            mCallback.onVideoChanged(mInfoList.get(curIndex));
        }
        mCurMediaPlayer = mPlayerList.get(curIndex);
        mCurMediaPlayer.setSurface(surface);
        mCurMediaPlayer.start();
    }

    public void stop() {
        mCurMediaPlayer.stop();
    }

    public void release() {
        for (int i = 0; i < mPlayerList.size(); i++) {
            mPlayerList.get(i).release();
        }
    }

    public void setVolume(float volume) {
        for (int i = 0; i < mPlayerList.size(); i++) {
            MediaPlayer mediaPlayer = mPlayerList.get(i);
            mediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        curIndex++;
        if (curIndex >= mSrcList.size()) {
            curIndex = 0;
            if (mCallback != null) {
                mCallback.onCompletion(mp);
            }
        }
        swithPlayer(mp);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    public interface IMediaCallback {
        /**
         * callback when all the player prepared
         */
        void onVideoPrepare();

        /**
         * callback when player start
         */
        void onVideoStart();

        /**
         * callback when player pause
         */
        void onVideoPause();

        /**
         * callback when all the videos have been played
         *
         * @param mp
         */
        void onCompletion(MediaPlayer mp);

        /**
         * callback when video changed
         *
         * @param info
         */
        void onVideoChanged(VideoInfo info);
    }
}
