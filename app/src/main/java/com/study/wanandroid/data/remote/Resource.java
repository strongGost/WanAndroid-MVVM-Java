package com.study.wanandroid.data.remote;

public class Resource {
    private UIState state;
    private String msg;

    public Resource() {}

    public Resource(UIState state,  String msg) {
        this.state = state;
        this.msg = msg;
    }

    public static  Resource loading() {
        return new Resource(UIState.LOADING, "loading...");
    }

    public static  Resource error(String msg) {
        return new Resource(UIState.ERROR, msg);
    }

    public static  Resource success(String msg) {
        return new Resource(UIState.SUCCESS, msg);
    }

    public static  Resource none() {
        return new Resource(UIState.EMPTY, "No more data!");
    }
    public static  Resource outLogin() {
        return new Resource(UIState.NEED_LOGIN, "Need log in");
    }

    public UIState getState() {
        return state;
    }

    public String getMsg() {
        return msg;
    }
}
