package com.huya.marksman.util.rxretrofit;

import android.support.annotation.NonNull;


import com.huya.marksman.util.rxretrofit.OkHttpHelper;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit帮助类
 */
public class RetrofitHelper {

    private static Retrofit.Builder sBuilder;
    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    public static <T> T createApi(Class<T> clazz, String baseUrl) {
        return createBuilder().baseUrl(baseUrl)
                .client(createHttpClientBuilder().build())
                .build()
                .create(clazz);
    }

    public static Retrofit.Builder getRetrofitBuilder() {
        if (sBuilder == null) {
            synchronized (Retrofit.Builder.class) {
                if (sBuilder == null) {
                    sBuilder = createBuilder();
                }
            }
        }
        return sBuilder;
    }


    /**
     * 创建"基本配置"的Retrofit.Builder
     */
    @NonNull
    public static Retrofit.Builder createBuilder() {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    /**
     * 创建"基本配置"的OkHttpClient.Builder
     */
    @NonNull
    public static OkHttpClient.Builder createHttpClientBuilder() {
        return OkHttpHelper.cloneOkHttpClient().newBuilder();
    }
}
