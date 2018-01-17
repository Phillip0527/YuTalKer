package com.im.yutalker.factory.presenter.account;

import android.text.TextUtils;

import com.im.yutalker.common.Common;
import com.im.yutalker.factory.R;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.helper.AccountHelper;
import com.im.yutalker.factory.model.api.account.RegisterModel;
import com.im.yutalker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.regex.Pattern;

/**
 * Created by Phillip on 2018/1/16.
 */

public class RegisterPresenter extends BasePresenter<RegisterContract.View>
        implements RegisterContract.Presenter, DataSource.CallBack {

    public RegisterPresenter(RegisterContract.View view) {
        super(view);
    }

    @Override
    public void register(String phone, String name, String password) {
        // 调用开始方法，在start()中默认启动loading
        start();

        //得到View接口
        RegisterContract.View view = getView();

        if (!checkMobile(phone)) {
            // 手机号错误
            view.showError(R.string.data_account_register_invalid_parameter_mobile);
        } else if (password.length() < 6) {
            // 密码错误
            view.showError(R.string.data_account_register_invalid_parameter_password);
        } else if (name.length() < 2) {
            // 昵称错误
            view.showError(R.string.data_account_register_invalid_parameter_name);
        } else {
            // 网络请求

            // 构造model，进行请求调用
            RegisterModel model = new RegisterModel(phone, name, password);
            // 进行网络请求
            AccountHelper.register(model, this);
        }
    }

    /**
     * 检查手机号是否合法
     *
     * @param phone 手机号码
     * @return 合法为true
     */
    @Override
    public boolean checkMobile(String phone) {
        // 手机号不为空，且满足正则表达式
        return !TextUtils.isEmpty(phone) && Pattern.matches(Common.Constance.REGEX_MOBILE, phone);
    }

    @Override
    public void onDataLoaded(Object o) {
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        // 此时是从网络回送回来的 不保证是主线程
        // 调用主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用主界面注册成功
                view.registerSuccess();
            }
        });

    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final RegisterContract.View view = getView();
        if (view == null)
            return;
        // 此时是从网络回送回来的 不保证是主线程
        // 调用主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用主界面注册成功
                view.showError(strRes);
            }
        });

    }
}
