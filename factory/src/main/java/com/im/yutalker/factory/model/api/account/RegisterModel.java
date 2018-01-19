package com.im.yutalker.factory.model.api.account;

/**
 * 注册请求使用的model
 * Created by Phillip on 2018/1/17.
 */

public class RegisterModel {

    private String account;
    private String password;
    private String name;
    private String pushId;

    public RegisterModel(String account, String name, String password) {
        this(account, name, password, null);
    }

    public RegisterModel(String account, String name, String password, String pushId) {
        this.account = account;
        this.name = name;
        this.password = password;
        this.pushId = pushId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    @Override
    public String toString() {
        return "RegisterModel{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", pushId='" + pushId + '\'' +
                '}';
    }
}
