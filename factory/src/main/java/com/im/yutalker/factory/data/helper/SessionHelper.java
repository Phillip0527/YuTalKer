package com.im.yutalker.factory.data.helper;

import com.im.yutalker.factory.model.dp.Session;
import com.im.yutalker.factory.model.dp.Session_Table;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.model.dp.User_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * 会话的辅助工具类
 * Created by Phillip on 2018/2/3.
 */

public class SessionHelper {
    /**
     * 从本地查询Session
     * @param id 会话id
     * @return 会话
     */
    public static Session findFromLocal(String id) {
        return SQLite.select()
                .from(Session.class)
                .where(Session_Table.id.eq(id))
                .querySingle();
    }
}
