package com.huya.marksman.data.userdao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by charles on 2018/6/25.
 */

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUser(User user);

    @Update
    int updateUser(User user);

    @Query("SELECT * FROM users")
    List<User> getUsers();

    @Query("SELECT * FROM Users WHERE userid = :id")
    User getUserById(String id);

    @Query("DELETE FROM Users WHERE userid = :id")
    int deleteUserById(String id);

    @Query("DELETE FROM Users")
    int deleteUsers();
}
