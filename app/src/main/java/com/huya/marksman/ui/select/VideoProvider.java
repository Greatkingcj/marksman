package com.huya.marksman.ui.select;

import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import com.charles.base.BaseApp;
import com.charles.base.utils.TimeUtil;
import com.charles.editor.entry.VideoInfo;
import com.huya.marksman.MarkApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by charles on 2018/9/2.
 */

public class VideoProvider {
    public static final Uri MEDIA_URI = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

    public static String[] mediaColumns = {
            MediaStore.Video.Media._ID ,
            MediaStore.Video.Media.DATA ,
            MediaStore.Video.Media.MIME_TYPE ,
            MediaStore.Video.Media.DURATION ,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_ADDED};

    public List<VideoInfo> loadVideoList(){
        List<VideoInfo> list = new ArrayList<>();
        try {
            MarkApplication application = (MarkApplication) MarkApplication.application();
            Cursor cursor = application.getContentResolver().query(MEDIA_URI , mediaColumns , null , null , null);
            if (cursor != null ){
                while (cursor.moveToNext()){
                    VideoInfo video = new VideoInfo();
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    long duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    int width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT));
                    long date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));

                    if (size == 0 || duration <= 1000){
                        continue;
                    }

                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    try {
                        //排除异常视频
                        retriever.setDataSource(BaseApp.application() , Uri.fromFile(new File(path)));

                        video.id = id;
                        video.videoPath = path;
                        video.mimeType = mimeType;
                        video.duration = duration;
                        video.fileSize = size;
                        video.width = width;
                        video.height = height;
                        video.date = TimeUtil.stampToDate(date * 1000);
                        list.add(video);
                    }catch (Exception e){

                    }finally {
                        retriever.release();
                    }
                }
                cursor.close();
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
        }finally {
            return assemblyData(list);
        }
    }

    private List<VideoInfo> assemblyData(List<VideoInfo> list) {
        if (list == null || list.size() == 0) {
            return new ArrayList<VideoInfo>();
        }
        Collections.sort(list , (i1 , i2)->{
            return i2.date.compareTo(i1.date);
        });

        List<VideoInfo> result = new ArrayList<>();

        VideoInfo header = null;
        for (int i = 0; i < list.size(); i++) {
            if (i == 0){
                header = new VideoInfo();
                header.isHeader = true;
                header.date = list.get(i).date;
                result.add(header);
                result.add(list.get(i));
            }else{
                VideoInfo pre = list.get(i -1);
                VideoInfo current = list.get(i);
                if (pre.date.equals(current.date)){
                    result.add(current);
                }else{
                    header = new VideoInfo();
                    header.isHeader = true;
                    header.date = list.get(i).date;
                    result.add(header);
                    result.add(list.get(i));
                }
            }
        }
        return result;
    }
}
