package com.im.yutalker.factory.data.message;

import android.support.annotation.NonNull;

import com.im.yutalker.factory.data.BaseDbRepository;
import com.im.yutalker.factory.model.dp.Session;
import com.im.yutalker.factory.model.dp.Session_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 最近聊天列表仓库是对SessionDataSource的实现
 * Created by xxx on 2018/4/17.
 */

public class SessionRepository extends BaseDbRepository<Session> implements SessionDataSource {

    @Override
    public void load(SuccessCallBack<List<Session>> callback) {
        super.load(callback);
        // 数据库查询
        SQLite.select()
                .from(Session.class)
                .orderBy(Session_Table.modifyAt, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();

        // 查询出来的倒序 10 9 8 复写insert后变成 8 9 10
        // 解决办法 复写 回来的list方法  先反转一下集合  在调用父类的方法
    }

    @Override
    protected boolean isRequired(Session session) {
        // true 表示不需要过滤 所有的会话都需要
        return true;
    }

    @Override
    protected void insert(Session session) {
        // 复写方法，让新的数据加到头部
        dataList.addFirst(session);
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Session> tResult) {
        // 复写数据库回来的方法，进行一次数据反转
        Collections.reverse(tResult);
        // 反转后再调用父类方法
        super.onListQueryResult(transaction, tResult);
    }
}
