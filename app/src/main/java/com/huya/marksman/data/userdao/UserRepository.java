package com.huya.marksman.data.userdao;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.huya.marksman.api.ApiResponse;
import com.huya.marksman.api.GithubApi;
import com.huya.marksman.data.userdao.UserDao;
import com.huya.marksman.vo.Resource;
import com.huya.marksman.data.userdao.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by charles on 2018/6/25.
 */

public class UserRepository implements UserDataSource{

    private static UserRepository INSTANCE = null;

    private final UserDataSource userDataSource;

    Map<String, User> mCachedUsers;

    boolean mCacheIsDirty = false;

    private UserRepository(UserDataSource userLocalDataSource) {
        userDataSource = userLocalDataSource;
    }

    public static UserRepository getInstance(UserDataSource userDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new UserRepository(userDataSource);
        }
        return INSTANCE;
    }


    @Override
    public void getUsers(@NonNull LoadUsersCallback callback) {
        if (mCachedUsers != null && !mCacheIsDirty) {
            callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
            return;
        }

        if (mCacheIsDirty) {
            // If the cache is dirty we need to fetch new data from the network.
            //getusersFromRemoteDataSource(callback);
        } else {
            // Query the local storage if available. If not, query the network.
            userDataSource.getUsers(new LoadUsersCallback() {
                @Override
                public void onUsersLoaded(List<User> users) {
                    refreshCache(users);
                    callback.onUsersLoaded(new ArrayList<>(mCachedUsers.values()));
                }

                @Override
                public void onDataNotAvailable() {
                    callback.onDataNotAvailable();
                    //getusersFromRemoteDataSource(callback);
                }
            });
        }
    }

    @Override
    public void getUser(@NonNull String userId, @NonNull GetUserCallback callback) {
        checkNotNull(userId);
        checkNotNull(callback);

        User cacheduser = getuserWithId(userId);

        // Respond immediately with cache if available
        if (cacheduser != null) {
            callback.onUserLoaded(cacheduser);
            return;
        }

        // Load from server/persisted if needed.

        // Is the user in the local data source? If not, query the network.
        userDataSource.getUser(userId, new GetUserCallback() {
            @Override
            public void onUserLoaded(User user) {
                // Do in memory cache update to keep the app UI up to date
                if (mCachedUsers == null) {
                    mCachedUsers = new LinkedHashMap<>();
                }
                mCachedUsers.put(user.getId(), user);
                callback.onUserLoaded(user);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
                //server 处理
            }
        });
    }

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        userDataSource.saveUser(user);

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), user);
    }

    @Override
    public void deleteAllUsers() {
        userDataSource.deleteAllUsers();

        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.clear();
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        userDataSource.deleteUser(checkNotNull(userId));

        mCachedUsers.remove(userId);
    }

    private void refreshCache(List<User> users) {
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.clear();
        for (User user : users) {
            mCachedUsers.put(user.getId(), user);
        }
        mCacheIsDirty = false;
    }

    @Nullable
    private User getuserWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedUsers == null || mCachedUsers.isEmpty()) {
            return null;
        } else {
            return mCachedUsers.get(id);
        }
    }
}
