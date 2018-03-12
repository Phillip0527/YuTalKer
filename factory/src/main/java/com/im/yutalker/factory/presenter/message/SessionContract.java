package com.im.yutalker.factory.presenter.message;

import com.im.yutalker.factory.model.dp.Session;
import com.im.yutalker.factory.presenter.BaseContract;

/**
 * Created by Phillip on 2018/3/6.
 */

public interface SessionContract {
    // 不需要定义方法，开始就start()
    interface Presenter extends BaseContract.Presenter {

    }

    interface View extends BaseContract.RecyclerView<Presenter, Session> {

    }
}
