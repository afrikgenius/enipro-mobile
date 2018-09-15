package com.enipro.presentation.utility;

public class NetworkState {


    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED
    }

    private final Status status;
    private final String msg;

    public static final NetworkState LOADING;
    public static final NetworkState LOADED;

    public NetworkState(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    static {
        LOADED = new NetworkState(Status.SUCCESS, "Success");
        LOADING = new NetworkState(Status.RUNNING, "Running");
    }

    public static NetworkState error(String message) {
        return new NetworkState(Status.FAILED, message);
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }
}
