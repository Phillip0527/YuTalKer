package com.im.yutalker.factory.presenter.search;

import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.presenter.BasePresenter;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.util.List;

import retrofit2.Call;

/**
 * 搜索人的逻辑实现
 * Created by Phillip on 2018/1/24.
 */

public class SearchUserPresenter extends BasePresenter<SearchContract.SearchUserView>
        implements SearchContract.Presenter, DataSource.CallBack<List<UserCard>> {

    private Call searchCall;

    public SearchUserPresenter(SearchContract.SearchUserView view) {
        super(view);
    }

    @Override
    public void search(String content) {
        start();
        Call call = searchCall;
        if (call != null && !call.isCanceled()) {
            // 如果有上一次的请求，并且没有取消，则调用取消操作
            call.cancel();
        }
        searchCall = UserHelper.search(content, this);
    }

    @Override
    public void onDataLoaded(final List<UserCard> userCards) {
        // 搜索成功
        final SearchContract.SearchUserView view = getView();
        if (view != null) {
            Run.onUiAsync(new Action() {
                @Override
                public void call() {
                    view.onSearchDone(userCards);
                }
            });
        }

    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        // 搜索失败
        final SearchContract.SearchUserView view = getView();
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
