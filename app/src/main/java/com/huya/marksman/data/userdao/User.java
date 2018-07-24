package com.huya.marksman.data.userdao;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import android.support.annotation.NonNull;

import java.util.UUID;

/**
 *
 * @author charles
 * @date 2018/6/25
 */

@Entity(tableName = "users")
public final class User {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "userid")
    private String id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "phonenum")
    private String phoneNum;

    @NonNull
    @ColumnInfo(name = "email")
    private String email;

    public User(String name, String phoneNum, String email) {
        this(UUID.randomUUID().toString(), name, phoneNum, email);
    }

    public User(String id, String name, String phonenum, String email) {
        this.id = id;
        this.name = name;
        this.phoneNum = phonenum;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    public String getPhoneNum() {
        return phoneNum;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setPhoneNum(@NonNull String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }
}
