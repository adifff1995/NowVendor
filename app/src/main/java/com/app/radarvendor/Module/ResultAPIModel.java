package com.app.radarvendor.Module;

public class ResultAPIModel<T> {
    public T data;
    public ErrorModel error;
    public String message;
    public String token;
    public boolean success;
    public int status;
}
