package com.im.yutalker.factory.data.message;

import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.DbDataSource;
import com.im.yutalker.factory.model.dp.Message;

/**
 * 消息的数据源定义：他的实现是MessageRepository
 * 关注的对象是Message表
 * Created by Phillip on 2018/2/11.
 */

public interface MessageDataSource extends DbDataSource<Message> {
}
