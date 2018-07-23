package com.huya.marksman.repository;

import android.arch.lifecycle.LiveData;

import com.huya.marksman.api.ApiResponse;
import com.huya.marksman.api.GithubApi;
import com.huya.marksman.db.UserDao;
import com.huya.marksman.vo.Resource;
import com.huya.marksman.vo.User;

import java.util.concurrent.Executor;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by charles on 2018/6/25.
 */

public class UserRepository {

    private UserDao userDao;
    private Executor executor;

    public UserRepository(UserDao userDao, Executor executor) {
        this.userDao = userDao;
        this.executor = executor;
    }

    public LiveData<User> getUser(long userId) {
        refreshUser(userId);
        return userDao.load(userId);
    }

    private void refreshUser(final long userId) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                boolean userExists = userDao.load(userId) != null;
                if (!userExists) {
                    GithubApi.getUser(userId).subscribe(new Observer<ApiResponse<User>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ApiResponse<User> value) {
                            userDao.save(value.getBody());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
                }
            }
        });
    }

    public LiveData<Resource<User>> loadUser(final long userId) {
        return new NetworkBoundResource<User, User>() {

            @Override
            protected void saveCallResult(User item) {
                userDao.save(item);
            }

            @Override
            protected boolean shouldFetch(User data) {
                return userDao.load(userId) == null;
            }

            @Override
            protected LiveData<User> loadFromDb() {
                return userDao.load(userId);
            }

            @Override
            protected LiveData<ApiResponse<User>> createCall() {
                //return GithubApi.getUser(userId);
                //return userDao.load(userId);
                return null;
            }

        }.getAsLiveData();
    }
}
