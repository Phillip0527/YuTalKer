package com.im.yutalker.factory.presenter.contact;

import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.presenter.BaseContract;

/**
 * Created by Phillip on 2018/1/29.
 */

public interface ContactContract {
    // 不需要定义方法，开始就start()
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, User> {

    }
}
