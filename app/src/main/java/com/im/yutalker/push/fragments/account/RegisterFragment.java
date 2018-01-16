package com.im.yutalker.push.fragments.account;



import android.content.Context;

import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.factory.presenter.account.RegisterContract;
import com.im.yutalker.factory.presenter.account.RegisterPresenter;
import com.im.yutalker.push.R;

/**
 * 注册的界面
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter> implements RegisterContract.View {
    private AccountTrigger accountTrigger;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到activity的引用
        accountTrigger = (AccountTrigger) context;
    }

    @Override
    protected RegisterContract.Presenter initPresenter() {
        return new RegisterPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    public void registerSuccess() {

    }
}
