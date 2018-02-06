package com.im.yutalker.factory.presenter.contact;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

import com.im.yutalker.common.widget.recycler.RecyclerAdapter;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.data.user.ContactDataSource;
import com.im.yutalker.factory.data.user.ContactRepository;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.model.dp.AppDataBase;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.model.dp.User_Table;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.factory.presenter.BasePresenter;
import com.im.yutalker.factory.presenter.BaseRecyclerPresenter;
import com.im.yutalker.factory.presenter.BaseSourcePresenter;
import com.im.yutalker.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phillip on 2018/1/29.
 */

public class ContactPresenter extends BaseSourcePresenter<User, User, ContactDataSource, ContactContract.View> implements ContactContract.Presenter, DataSource.SuccessCallBack<List<User>> {

    public ContactPresenter(ContactContract.View view) {
        // 初始化数据仓库
        super(new ContactRepository(), view);
    }

    @Override
    public void start() {
        super.start();
        // 加载网络数据
        UserHelper.refreshContacts();
    }


    // 数据对比方法
    private void diff(List<User> oldList, List<User> newList) {
        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(oldList, newList);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 在对比完成后进行数据的赋值
        getView().getRecyclerAdapter().replace(newList);

        // 尝试刷新界面
        result.dispatchUpdatesTo(getView().getRecyclerAdapter());
        getView().onAdapterDataChanged();
    }

    // 运行到这里的时候是子线程
    @Override
    public void onDataLoaded(List<User> users) {
        // 无论怎么操作，数据变更，最终都会通知到这里
        final ContactContract.View view = getView();
        if (view == null)
            return;
        RecyclerAdapter<User> adapter = view.getRecyclerAdapter();
        List<User> old = adapter.getItems();

        // 进行数据对比
        DiffUtil.Callback callback = new DiffUiDataCallback<>(old, users);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);

        // 调用基类方法进行界面刷新
        refreshData(result, users);

    }
}
