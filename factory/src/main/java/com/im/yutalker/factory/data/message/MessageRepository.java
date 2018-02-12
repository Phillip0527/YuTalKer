package com.im.yutalker.factory.data.message;

import android.support.annotation.NonNull;

import com.im.yutalker.factory.data.BaseDbRepository;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.Message_Table;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import java.util.Collections;
import java.util.List;

/**
 * 跟某人聊天时候的聊天列表
 * 关注的内容一定是我发给这个人的，或者这个人发给我的
 * Created by Phillip on 2018/2/11.
 */

public class MessageRepository extends BaseDbRepository<Message> implements MessageDataSource {

    // 聊天对象的Id
    private String receiverId;

    public MessageRepository(String receiverId) {
        super();
        this.receiverId = receiverId;
    }

    @Override
    public void load(SuccessCallBack<List<Message>> callback) {
        super.load(callback);

        SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(receiverId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(receiverId))
                .orderBy(Message_Table.createAt, false)
                .limit(30)
                .async()
                .queryListResultCallback(this)
                .execute();
    }

    @Override
    protected boolean isRequired(Message message) {
        // receiverId 如果是发送者，那么Group==null情况下一定是发送给我的消息
        // 如果消息的接收者不为空，那么一定是发送给我或者某个人
        // receiverId如果是接收者，那么是我发送给这个人的消息
        return (receiverId.equalsIgnoreCase(message.getSender().getId())
                && message.getGroup() == null)
                || (message.getReceiver() != null
                && receiverId.equalsIgnoreCase(message.getReceiver().getId()));
    }

    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Message> tResult) {
        // 反转返回的集合
        Collections.reverse(tResult);
        // 然后再调度父类
        super.onListQueryResult(transaction, tResult);
    }
}
