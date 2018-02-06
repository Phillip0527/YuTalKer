package com.im.yutalker.factory.data.user;


import com.im.yutalker.factory.data.BaseDbRepository;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.helper.DbHelper;
import com.im.yutalker.factory.model.dp.BaseDbModel;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.model.dp.User_Table;
import com.im.yutalker.factory.persistence.Account;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * 联系人仓库
 * Created by Phillip on 2018/2/3.
 */

public class ContactRepository extends BaseDbRepository<User> implements ContactDataSource {

    @Override
    public void load(DataSource.SuccessCallBack<List<User>> callback) {
        super.load(callback);
        // DbFlow加载本地数据库数据
        SQLite.select()
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .limit(100)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    /**
     * 检查一个User是否是我需要关注的数据
     *
     * @param user 用户
     * @return True表示是需要关注的
     */
    protected boolean isRequired(User user) {
        return user.isFollow() && !user.getId().equals(Account.getUserId());
    }
}
