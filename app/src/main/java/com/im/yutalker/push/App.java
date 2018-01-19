package com.im.yutalker.push;

import com.igexin.sdk.PushManager;
import com.im.yutalker.common.app.Application;
import com.im.yutalker.factory.Factory;

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
}
