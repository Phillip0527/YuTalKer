package com.im.yutalker.factory.presenter.contact;

import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.presenter.BaseContract;

import java.util.List;

/**
 * Created by Phillip on 2018/1/26.
 */

public interface FollowContract {
    interface Presenter extends BaseContract.Presenter {
        void follow(String userId);
    }

    interface View extends BaseContract.View<Presenter> {
        void onFollowSucceed(UserCard userCard);
    }
}
