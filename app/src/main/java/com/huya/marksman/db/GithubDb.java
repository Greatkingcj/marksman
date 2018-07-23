package com.huya.marksman.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.huya.marksman.vo.User;

/**
 * Created by charles on 2018/6/25.
 */

@Database(entities = {User.class}, version = 1)
public abstract class GithubDb extends RoomDatabase{
    public abstract UserDao userDao();
}
