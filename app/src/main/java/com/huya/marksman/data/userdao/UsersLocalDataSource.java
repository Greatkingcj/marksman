package com.huya.marksman.data.userdao;

import android.support.annotation.NonNull;

import com.huya.marksman.util.AppExecutors;

import java.util.List;

import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by charles on 2018/7/24.
 */

public class UsersLocalDataSource implements UserDataSource{

    private static volatile UsersLocalDataSource INSTANCE;

    private UserDao userDao;

    private AppExecutors appExecutors;

    public static UsersLocalDataSource getInstance(AppExecutors appExecutors
            ,UserDao userDao) {
        if (INSTANCE == null) {
            synchronized (UsersLocalDataSource.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UsersLocalDataSource(appExecutors, userDao);
                }
            }
        }

        return INSTANCE;
    }

    private UsersLocalDataSource(AppExecutors appExecutors, UserDao userDao) {
        this.appExecutors = appExecutors;
        this.userDao = userDao;
    }

    @Override
    public void getUsers(@NonNull LoadUsersCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<User> users = userDao.getUsers();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (users.isEmpty()) {
                            callback.onDataNotAvailable();
                        } else {
                            callback.onUsersLoaded(users);
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getUser(@NonNull String userId, @NonNull GetUserCallback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final User user = userDao.getUserById(userId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (user != null) {
                            callback.onUserLoaded(user);
                        } else {
                            callback.onDataNotAvailable();
                        }
                    }
                });
            }
        };
        appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveUser(@NonNull User user) {
        checkNotNull(user);
        Runnable saveRunnable = new Runnable() {
            @Override
            public void run() {
                userDao.insertUser(user);
            }
        };

        appExecutors.diskIO().execute(saveRunnable);
    }

    @Override
    public void deleteAllUsers() {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                userDao.deleteUsers();
            }
        };
        appExecutors.diskIO().execute(deleteRunnable);
    }

    @Override
    public void deleteUser(@NonNull String userId) {
        Runnable deleteRunnable = new Runnable() {
            @Override
            public void run() {
                userDao.deleteUserById(userId);
            }
        };
        appExecutors.diskIO().execute(deleteRunnable);
    }
}
