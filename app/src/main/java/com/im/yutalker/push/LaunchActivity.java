package com.im.yutalker.push;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.im.yutalker.common.app.Activity;
import com.im.yutalker.push.activities.MainActivity;
import com.im.yutalker.push.fragments.assist.PermissionsFragment;

public class LaunchActivity extends Activity {


    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 检查权限
        if ( PermissionsFragment.haveAllPerms(this, getSupportFragmentManager())){
            MainActivity.show(this);
            finish();
        }
    }
}
