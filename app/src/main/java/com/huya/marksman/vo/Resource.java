package com.huya.marksman.vo;


import org.jetbrains.annotations.Nullable;

/**
 * Created by charles on 2018/6/26.
 */

public class Resource<T> {
    public final Status status;
    public final T data;
    public final String message;

    private Resource(@Nullable Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, msg);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }
}
