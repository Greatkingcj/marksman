package com.huya.marksman.widget.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.charles.base.utils.imageloader.GlideApp;
import com.huya.marksman.R;
import com.huya.marksman.util.ScreenUtil;

import java.util.concurrent.ExecutionException;

/**
 * 使用Glide加载图片
 *
 * @author charles
 */

public class GlideImageLoader {

    public static DrawableTransitionOptions normalTransitionOptions = new DrawableTransitionOptions()
            .crossFade();

    public static void loadImage(Context context, final ImageView imageView, String url) {
        try {
            GlideApp.with(context)
                    .load(url)
                    .placeholder(new ColorDrawable(context.getResources().getColor(R.color.cardview_light_background)))
                    .into(imageView);
        }catch (Throwable ignored){

        }
    }

    public static void loadImage(Context context, final ImageView imageView, String url, int defRes) {
        loadImage(context, imageView, url, context.getResources().getDrawable(defRes));
    }

    public static void loadImage(Context context, final ImageView imageView, String url, Drawable defRes) {
        try {
            GlideApp.with(context)
                    .load(url)
                    .transition(normalTransitionOptions)
                    .placeholder(defRes)
                    .error(defRes)
                    .into(imageView);
        }catch (Throwable ignored){

        }
    }

    public static void loadCircleImage(Context context, final ImageView imageView, String url, int defRes) {
        loadCircleImage(context, imageView, url, defRes, defRes);
    }

    // 加载圆形图片
    public static void loadCircleImage(Context context, final ImageView imageView, String url, int defRes, int errorRes) {
        try {
            GlideApp.with(context)
                    .load(url)
                    .transition(normalTransitionOptions)
                    .circleCrop()
                    .placeholder(defRes)
                    .error(errorRes)
                    .into(imageView);
        }catch (Throwable ignored){

        }
    }

    public static void getImage(Context context, String url, final int width, final int height) {
        // TODO 回调
        try {
            Bitmap bitmap = GlideApp.with(context)
                    .asBitmap() // 必须
                    .load(url)
                    .transform(new MultiTransformation<>(new CircleCrop()))
                    .submit(width, height)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void loadRoundImage(Context context, final ImageView imageView, String url, int radius) {

        try {
            RequestOptions options = new RequestOptions();
            options.centerCrop().transform(new RoundedCorners(ScreenUtil.dp2px(radius)));
            GlideApp.with(context)
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }catch (Throwable ignored){

        }
    }

    public static void loadRoundImage(Context context, final ImageView imageView, String url, int radius, int defRes) {
        loadRoundImage(context, imageView, url, radius, context.getResources().getDrawable(defRes));
    }

    public static void loadRoundImage(Context context, final ImageView imageView, String url, int radius, Drawable defRes) {
        try {
            RequestOptions options = new RequestOptions();
            options.centerCrop().transform(new RoundedCorners(ScreenUtil.dp2px(radius)));
            GlideApp.with(context)
                    .load(url)
                    .apply(options)
                    .placeholder(defRes)
                    .error(defRes)
                    .into(imageView);
        }catch (Throwable ignored){

        }
    }
}
