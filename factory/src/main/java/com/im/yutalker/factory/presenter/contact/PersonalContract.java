package com.im.yutalker.factory.presenter.contact;

import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.presenter.BaseContract;

/**
 * Created by Phillip on 2018/1/30.
 */

public interface PersonalContract {
    interface Presenter extends BaseContract.Presenter {
        // 获取用户信息
        User getUserPersonal();

    }

    interface View extends BaseContract.View<Presenter> {
        String getUserId();

        // 数据加载完成
        void onLoadDone(User user);

        // 是否发起聊天
        void allowSayHello(boolean isAllow);

        // 设置关注状态
        void setFollowStatus(boolean isFollow);
    }
}
