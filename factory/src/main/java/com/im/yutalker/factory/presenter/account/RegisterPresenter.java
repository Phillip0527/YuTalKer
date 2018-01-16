package com.im.yutalker.factory.presenter.account;

import com.im.yutalker.factory.presenter.BasePresenter;

/**
 * Created by Phillip on 2018/1/16.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {

    }

    @Override
    public boolean checkMobile(String phone) {
        return false;
    }
}
