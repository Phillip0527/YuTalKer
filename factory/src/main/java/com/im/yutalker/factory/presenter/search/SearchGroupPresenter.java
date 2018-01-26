package com.im.yutalker.factory.presenter.search;

import com.im.yutalker.factory.presenter.BasePresenter;

/**
 * 搜索群的逻辑实现
 * Created by Phillip on 2018/1/24.
 */

public class SearchGroupPresenter extends BasePresenter<SearchContract.SearchGroupView> implements SearchContract.Presenter {
    public SearchGroupPresenter(SearchContract.SearchGroupView view) {
        super(view);
    }

    @Override
    public void search(String content) {

    }
}
