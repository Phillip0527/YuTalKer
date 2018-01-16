package com.im.yutalker.push.activities;

import android.content.Intent;

import com.im.yutalker.common.app.Activity;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.account.LoginFragment;

public class UserActivity extends Activity {

    private Fragment mFragment;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mFragment = new LoginFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode, resultCode, data);
    }

}
