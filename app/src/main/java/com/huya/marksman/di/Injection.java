package com.huya.marksman.di;

import android.content.Context;

import com.huya.marksman.data.db.UserDatabase;
import com.huya.marksman.data.userdao.UserRepository;
import com.huya.marksman.data.userdao.UsersLocalDataSource;
import com.huya.marksman.util.AppExecutors;

/**
 * Created by charles on 2018/7/26.
 */

public class Injection {
    public static UserRepository provideUserRepository(Context context) {
        UserDatabase userDatabase = UserDatabase.getInstance(context);
        UsersLocalDataSource usersLocalDataSource = UsersLocalDataSource.getInstance(new AppExecutors(), userDatabase.userDao());
        return UserRepository.getInstance(usersLocalDataSource);
    }
}
