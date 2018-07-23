package com.huya.marksman.util.rxretrofit;

import android.content.Context;
import android.util.Log;

import com.huya.marksman.util.interceptor.LoggingInterceptor;

import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Okhttp辅助类
 *
 * @autor liangruijun
 * @date 2017/9/8 17:51
 */
public class OkHttpHelper {

    public final static String LOG_TAG = "OkHttpHelper";

    private static OkHttpClient sInternalClient;
    private static File sCacheDirectory;

    public static void init(Context context) {
        sCacheDirectory = new File(context.getExternalCacheDir(), "okhttp_cache");
    }

    public static OkHttpClient getOkHttpClient() {
        if (sInternalClient == null) {
            synchronized (OkHttpHelper.class) {
                if (sInternalClient == null) {
                    sInternalClient = applyOptions(new OkHttpClient.Builder());
                }
            }
        }
        return sInternalClient;
    }

    private static OkHttpClient applyOptions(final OkHttpClient.Builder builder) {
        int cacheSize = 100 * 1024 * 1024; // 100 MiB
        Cache cache = new Cache(sCacheDirectory, cacheSize);

        return builder.connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor()) // add logging as last interceptor
                .build();
    }

    public static OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    public static OkHttpClient cloneOkHttpClient() {
        OkHttpClient clone = getOkHttpClient().newBuilder().build();
        return clone;
    }

    /**
     * 创建使用 SSL 协议的 http client.
     */
    public static OkHttpClient cloneOkHttpsClient() {
        // 创建使用 SSL 协议的 http client.
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            sslContext.init(null, new TrustManager[]{tm}, null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "#cloneOkHttpsClient : ", e);
        }

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        OkHttpClient clone = getOkHttpClient().newBuilder()
                .sslSocketFactory(sslContext.getSocketFactory())
                .hostnameVerifier(hostnameVerifier)
                .build();
        return clone;
    }
}
