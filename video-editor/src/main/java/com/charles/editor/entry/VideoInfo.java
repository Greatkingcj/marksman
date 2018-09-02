package com.charles.editor.entry;

/**
 * Created by charles on 2018/8/31.
 */

public class VideoInfo {
    public boolean isHeader;
    public long id;
    public String mimeType;
    public long fileSize;
    public String date;
    public int rotation;
    public String thumbPath;
    public int width;
    public int height;
    public String videoUrl;
    public long duration;
    public String previewVideoUrl;
    public String videoPath;

    @Override
    public String toString() {
        return "ApiVideo{width:" + width + ", " +
                "height:" + height + ", " +
                "videoUrl:" + videoUrl + ", " +
                "videoPath:" + videoPath + ", " +
                "duration:" + duration +"}";
    }
}
