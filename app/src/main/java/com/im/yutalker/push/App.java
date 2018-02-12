package com.im.yutalker.push;

import android.content.Context;

import com.igexin.sdk.PushManager;
import com.im.yutalker.common.app.Application;
import com.im.yutalker.factory.Factory;
import com.im.yutalker.push.activities.AccountActivity;

/**
 * Created by Phillip on 2018/1/9.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 调用Factory初始化
        Factory.setup();
        // 调用推送初始化
        PushManager.getInstance().initialize(this);
    }

    @Override
    protected void showAccountActivity(Context context) {
        // 登录界面显示
        AccountActivity.show(context);
    }
}
