package com.im.yutalker.factory.presenter.contact;

import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by Phillip on 2018/1/26.
 */

public class FollowPresenter extends BasePresenter<FollowContract.View> implements FollowContract.Presenter, DataSource.CallBack<UserCard> {

    public FollowPresenter(FollowContract.View view) {
        super(view);
    }

    @Override
    public void follow(String userId) {
        start();
        UserHelper.follow(userId, this);
    }

    @Override
    public void onDataLoaded(final UserCard userCard) {
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onFollowSucceed(userCard);
                }
            });
        }
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final FollowContract.View view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.showError(strRes);
                }
            });
        }
    }
}
