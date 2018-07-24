package com.huya.marksman.data.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.huya.marksman.data.userdao.UserDao;
import com.huya.marksman.data.userdao.User;

/**
 * Created by charles on 2018/6/25.
 */

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class GithubDb extends RoomDatabase{
    public abstract UserDao userDao();
}
