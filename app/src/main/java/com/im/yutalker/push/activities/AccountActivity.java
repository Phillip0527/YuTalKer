package com.im.yutalker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.im.yutalker.common.app.Activity;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.account.UpdateInfoFragment;

public class AccountActivity extends Activity {

    private Fragment mFragment;

    /**
     * 账户Activity显示入口
     *
     * @param context Context
     */
    public static void show(Context context) {
        context.startActivity(new Intent(context, AccountActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mFragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode,resultCode,data);
    }
}
