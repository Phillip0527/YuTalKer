package com.im.yutalker.factory.presenter;

import android.support.annotation.StringRes;

/**
 * MVP模式中公共的基本契约
 * Created by Phillip on 2018/1/16.
 */

public interface BaseContract {
    interface View<T extends Presenter> {
        // 公共的：显示一个字符串错误
        void showError(@StringRes int str);

        // 公共的：显示进度条
        void showLoading();

        // 支持设置一个Presenter
        void setPresenter(T presenter);
    }

    interface Presenter {

        // 公用的开始触发
        void start();

        // 公用的销毁触发
        void destroy();
    }
}