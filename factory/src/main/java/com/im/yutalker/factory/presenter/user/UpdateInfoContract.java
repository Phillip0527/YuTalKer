package com.im.yutalker.factory.presenter.user;

import com.im.yutalker.factory.presenter.BaseContract;

/**
 * 更新用户信息的基本契约
 * Created by Phillip on 2018/1/22.
 */

public interface UpdateInfoContract {


    interface View extends BaseContract.View<Presenter> {
        // 回调成功
        void updateSucceed();
    }

    interface Presenter extends BaseContract.Presenter {
        // 更新
        void update(String photoFilePath, String desc, boolean isMan);
    }

}
