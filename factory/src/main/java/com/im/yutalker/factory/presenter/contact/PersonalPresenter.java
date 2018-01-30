package com.im.yutalker.factory.presenter.contact;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

/**
 * Created by Phillip on 2018/1/30.
 */

public class PersonalPresenter extends BasePresenter<PersonalContract.View> implements PersonalContract.Presenter {
    private User user;

    public PersonalPresenter(PersonalContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        super.start();
        // 个人界面用户数据优先从网络拉取
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                PersonalContract.View view = getView();
                if (view != null) {
                    String id = view.getUserId();
                    User user = UserHelper.searchFirstOfNet(id);
                    onLoaded(view, user);
                }
            }
        });

    }

    private void onLoaded(final PersonalContract.View view, final User user) {
        this.user = user;
        // 是否是我自己
        final boolean isSelf = user.getId().equalsIgnoreCase(Account.getUserId());
        // 是否已经关注
        final boolean isFollow = isSelf || user.isFollow();
        // 已经关注并且不是自己
        final boolean allowSayHello = isFollow && !isSelf;
        // 切换到UI线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                view.onLoadDone(user);
                view.setFollowStatus(isFollow);
                view.allowSayHello(allowSayHello);
            }
        });

    }

    @Override
    public User getUserPersonal() {
        return user;
    }
}
