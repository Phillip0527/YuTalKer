package com.im.yutalker.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.im.yutalker.factory.data.message.SessionDataSource;
import com.im.yutalker.factory.data.message.SessionRepository;
import com.im.yutalker.factory.model.dp.Session;
import com.im.yutalker.factory.presenter.BaseSourcePresenter;
import com.im.yutalker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 最近聊天列表的Presenter
 * Created by Phillip on 2018/3/7.
 */

public class SessionPresenter extends BaseSourcePresenter<Session, Session, SessionDataSource, SessionContract.View> implements SessionContract.Presenter {

    public SessionPresenter(SessionContract.View view) {
        super(new SessionRepository(), view);
    }

    @Override
    public void onDataLoaded(List<Session> sessions) {
        SessionContract.View view = getView();
        if (view == null)
            return;
        // 差异对比
        List<Session> old = view.getRecyclerAdapter().getItems();
        DiffUiDataCallback callback = new DiffUiDataCallback(old, sessions);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        // 刷新界面
        refreshData(result, sessions);
    }
}
