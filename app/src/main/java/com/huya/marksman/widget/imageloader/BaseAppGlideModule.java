package com.huya.marksman.widget.imageloader;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

/**
 * 自定义GlideModule
 *
 * @author ruijun
 * @date 2018/5/13上午11:01
 */

@GlideModule
public class BaseAppGlideModule extends AppGlideModule {

    /**
     * 设置内存缓存大小50M
     */
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int size = 50 * 1024 * 1024;
        builder.setMemoryCache(new LruResourceCache(size));
        builder.setLogLevel(Log.ERROR);
//        builder.setDiskCache(new ExternalPreferredCacheDiskCacheFactory(context, size));
    }

    /**
     * 关闭解析AndroidManifest
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
