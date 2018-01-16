package com.im.yutalker.factory.presenter.account;

import android.support.annotation.StringRes;

import com.im.yutalker.factory.presenter.BaseContract;

/**
 * Created by Phillip on 2018/1/16.
 */

public interface LoginContract {
    interface View extends BaseContract.View<Presenter>{

        // 登录成功
        void loginSuccess();

    }

    interface Presenter extends BaseContract.Presenter {

        // 登录
        void login(String phone, String name, String password);

    }
}
