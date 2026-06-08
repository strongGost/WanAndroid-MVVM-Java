package com.study.wanandroid.data.model;

import java.util.Objects;

public class ScoreBean {
    private int coinCount;
    private long date;
    private String desc;
    private int id;
    private String reason;
    private int type;
    private int userId;
    private String userName;

    public int getCoinCount() {
        return coinCount;
    }

    public void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScoreBean scoreBean = (ScoreBean) o;
        return coinCount == scoreBean.coinCount && date == scoreBean.date && id == scoreBean.id && type == scoreBean.type && userId == scoreBean.userId && Objects.equals(desc, scoreBean.desc) && Objects.equals(reason, scoreBean.reason) && Objects.equals(userName, scoreBean.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coinCount, date, desc, id, reason, type, userId, userName);
    }
}
