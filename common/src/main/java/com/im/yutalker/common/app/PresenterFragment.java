package com.im.yutalker.common.app;

import android.content.Context;

import com.im.yutalker.factory.presenter.BaseContract;

/**
 * Created by Phillip on 2018/1/16.
 */

public abstract class PresenterFragment<Presenter extends BaseContract.Presenter> extends Fragment implements BaseContract.View<Presenter> {
    protected Presenter mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 在界面onAttach之后就触发初始化Presenter
        initPresenter();
    }

    /**
     * 初始化Presenter
     *
     * @return Presenter
     */
    protected abstract Presenter initPresenter();

    @Override
    public void showError(int str) {
        // 显示错误,优先使用占位布局
        if (placeHolderView != null) {
            placeHolderView.triggerError(str);
        }else{
            Application.showToast(str);
        }
    }

    @Override
    public void showLoading() {
        if (placeHolderView != null) {
            placeHolderView.triggerLoading();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        // View中赋值Presenter
        mPresenter = presenter;
    }
}
