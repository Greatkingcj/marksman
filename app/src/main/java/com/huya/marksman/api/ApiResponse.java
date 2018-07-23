package com.huya.marksman.api;

/**
 * Created by charles on 2018/6/25.
 */

public class ApiResponse<T> {
    private T body;
    public String errorMessage;

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isSuccessful() {
        return true;
    }


}
