package com.charles.editor.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.charles.editor.entry.VideoInfo;

import java.io.File;

/**
 * Created by charles on 2018/8/31.
 */

public class VideoUtils {


    /**
     * @param videoUrl
     * @return return videoinfo according to videourl
     */
    public static VideoInfo getInfo(String videoUrl) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();;

        try {

            retriever.setDataSource(videoUrl);

            VideoInfo info = new VideoInfo();

            String w = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            info.width = Integer.parseInt(w);

            String h = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            info.height = Integer.parseInt(h);

            String rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
            info.height = Integer.parseInt(rotation);

            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            info.height = Integer.parseInt(duration);

            return info;
        } catch (Exception e) {
            return null;
        } finally {
            retriever.release();
        }

    }

    public static Bitmap getCoverBitmap(String videoUrl) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            File f = new File(videoUrl);
            if (f.exists() && f.isFile()) {
                retriever.setDataSource(videoUrl);
                return retriever.getFrameAtTime(0);
            }
        } catch (Exception e) {

        } finally {
            retriever.release();
        }

        return null;
    }
}
