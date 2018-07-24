package com.huya.marksman.api;

import android.arch.lifecycle.LiveData;

import com.huya.marksman.util.rxretrofit.OkHttpHelper;
import com.huya.marksman.util.rxretrofit.RxThreadComposeUtil;
import com.huya.marksman.data.repodao.Repo;
import com.huya.marksman.data.userdao.User;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by charles on 2018/6/25.
 */

public class GithubApi {
    public static final GithubApiService gitApiService;

    static {
        Retrofit.Builder builder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

        gitApiService = builder.baseUrl("https://api.github.com/")
                .client(OkHttpHelper.createOkHttpClient())
                .build()
                .create(GithubApiService.class);
    }

    public static Observable<ApiResponse<User>> getUser(long userId) {
        return gitApiService.getUser(userId + "")
                .compose(RxThreadComposeUtil.<ApiResponse<User>>applySchedulers());
    }

    public interface GithubApiService {

        @GET("users/{login}")
        Observable<ApiResponse<User>> getUser(@Path("login") String login);

        @GET("users/{login}/repos")
        LiveData<ApiResponse<List<Repo>>> getRepos(@Path("login") String login);
    }
}
