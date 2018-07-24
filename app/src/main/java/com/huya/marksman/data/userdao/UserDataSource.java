package com.huya.marksman.data.userdao;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by charles on 2018/7/24.
 */

public interface UserDataSource {

    interface LoadUsersCallback {

        void onUsersLoaded(List<User> users);

        void onDataNotAvailable();
    }

    interface GetUserCallback {

        void onUserLoaded(User user);

        void onDataNotAvailable();
    }

    void getUsers(@NonNull LoadUsersCallback callback);

    void getUser(@NonNull String taskId, @NonNull GetUserCallback callback);

    void saveUser(@NonNull User task);

    void deleteAllUsers();

    void deleteUser(@NonNull String userId);
}
