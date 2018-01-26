package com.im.yutalker.factory.presenter.search;

import com.im.yutalker.factory.model.card.GroupCard;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.presenter.BaseContract;

import java.util.List;

/**
 * Created by Phillip on 2018/1/24.
 */

public interface SearchContract {
    interface Presenter extends BaseContract.Presenter {
        // 搜索内容
        void search(String content);
    }

    // 搜索人的返回界面
    interface SearchUserView extends BaseContract.View<Presenter> {
        void onSearchDone(List<UserCard> userCards);
    }

    // 搜索群的返回界面
    interface SearchGroupView extends BaseContract.View<Presenter> {
        void onSearchDone(List<GroupCard> groupCards);
    }
}
