package com.im.yutalker.push.fragments.account;


import android.content.Context;
import android.widget.EditText;

import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.factory.presenter.account.LoginContract;
import com.im.yutalker.factory.presenter.account.LoginPresenter;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.MainActivity;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 登录的界面
 */
public class LoginFragment extends PresenterFragment<LoginContract.Presenter> implements LoginContract.View {
    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    private AccountTrigger accountTrigger;


    public LoginFragment() {
        // Required empty public constructor
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String password = mPassword.getText().toString();
        // 调用P层进行注册
        mPresenter.login(phone, password);
    }

    @OnClick(R.id.txt_go_register)
    void onGoRegisterClick() {
        // AccountActivity进行界面切换
        accountTrigger.triggerView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到activity的引用
        accountTrigger = (AccountTrigger) context;
    }

    @Override
    protected LoginContract.Presenter initPresenter() {
        return new LoginPresenter(this);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止loading
        mLoading.stop();
        // 让控件可以输入
        mPhone.setEnabled(true);
        mPassword.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);

    }

    @Override
    public void showLoading() {
        super.showLoading();
        // 正在进行时
        // 开始loading
        mLoading.start();
        // 让控件可以输入
        mPhone.setEnabled(false);
        mPassword.setEnabled(false);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void loginSuccess() {
        // 登录成功
        // 跳转到MainActivity界面
        MainActivity.show(getActivity());
        // 关闭当前界面
        getActivity().finish();
    }
}
