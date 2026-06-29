package com.study.wanandroid.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 封装类, 个人信息类
 */
public class MeInfo {
    @SerializedName("coinInfo")
    private CoinInfoBean coinInfo;

    // "userInfo"
    @SerializedName("userInfo")
    private UserBean userInfo;

    public CoinInfoBean getCoinInfo() { return coinInfo; }
    public void setCoinInfo(CoinInfoBean coinInfo) { this.coinInfo = coinInfo; }

    public UserBean getUserInfo() { return userInfo; }
    public void setUserInfo(UserBean userInfo) { this.userInfo = userInfo; }


    @Override
    public String toString() {
        return "MeInfo{" +
                "coinInfo=" + coinInfo +
                ", userInfo=" + userInfo +
                '}';
    }
}
