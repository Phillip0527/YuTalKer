package com.im.yutalker.push.fragments.account;



import android.content.Context;

import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.push.R;

/**
 * 登录的界面
 */
public class LoginFragment extends Fragment {
    private AccountTrigger accountTrigger;


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // 拿到activity的引用
        accountTrigger = (AccountTrigger) context;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_login;
    }

}
