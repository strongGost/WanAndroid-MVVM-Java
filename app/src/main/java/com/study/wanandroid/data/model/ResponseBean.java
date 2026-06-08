package com.study.wanandroid.data.model;

import com.study.wanandroid.utils.Constant;

/**
 * Auto-generated: 2026-03-07 12:10:58
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 * 请求响应类
 */
public class ResponseBean<T> {

    private T data;
    private int errorCode;
    private String errorMsg;
    public void setData(T data) {
        this.data = data;
    }
    public T getData() {
        return data;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public String getErrorMsg() {
        return errorMsg;
    }


    /**
     * errorCode = 0 代表返回响应成功
     * @return
     */
    public boolean isSuccess() {
        return this.errorCode == 0;
    }


    /**
     * 未登录的错误码为 -1001
     * @return 是否处于未登录状态
     */
    public boolean isOutLogin() {
        return this.errorCode == Constant.LOGIN_ERROR_CODE;
    }
}