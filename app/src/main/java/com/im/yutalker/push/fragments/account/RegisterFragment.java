package com.im.yutalker.push.fragments.account;


import android.content.Context;
import android.widget.EditText;

import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.factory.presenter.account.RegisterContract;
import com.im.yutalker.factory.presenter.account.RegisterPresenter;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.MainActivity;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注册的界面
 */
public class RegisterFragment extends PresenterFragment<RegisterContract.Presenter> implements RegisterContract.View {

    @BindView(R.id.edit_phone)
    EditText mPhone;
    @BindView(R.id.edit_name)
    EditText mName;
    @BindView(R.id.edit_password)
    EditText mPassword;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

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

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String phone = mPhone.getText().toString();
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();
        // 调用P层进行注册
        mPresenter.register(phone, name, password);
    }

    @OnClick(R.id.txt_go_login)
    void onGoLoginClick() {
        // AccountActivity进行界面切换
        accountTrigger.triggerView();
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止loading
        mLoading.stop();
        // 让控件可以输入
        mPhone.setEnabled(true);
        mName.setEnabled(true);
        mPassword.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);

    }

    @Override
    public void showLoading() {
        super.showLoading();
        // 正在进行时，正在注册界面不可操作
        // 开始loading
        mLoading.start();
        // 让控件可以输入
        mPhone.setEnabled(false);
        mName.setEnabled(false);
        mPassword.setEnabled(false);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void registerSuccess() {
        // 注册成功，账户自动登录
        // 跳转到MainActivity界面
        MainActivity.show(getActivity());
        // 关闭当前界面
        getActivity().finish();
    }
}
