package com.im.yutalker.factory.data.helper;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.api.message.MsgCreateModel;
import com.im.yutalker.factory.model.card.MessageCard;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.Message_Table;
import com.im.yutalker.factory.model.dp.Session;
import com.im.yutalker.factory.model.dp.Session_Table;
import com.im.yutalker.factory.net.NetWork;
import com.im.yutalker.factory.net.RemoteService;
import com.raizlabs.android.dbflow.sql.language.OperatorGroup;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 消息工具类
 * Created by Phillip on 2018/2/1.
 */

public class MessageHelper {
    // 从本地找消息
    public static Message findFromLocal(String id) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.id.eq(id))
                .querySingle();
    }

    // 发送是异步进行
    public static void push(final MsgCreateModel model) {
        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
                // 成功状态：如果是一个已经发送的消息，则不能重新发送
                // 正在发送状态：如果是一个正在发送的消息，则不能重新发送
                Message message = findFromLocal(model.getId());
                if (message != null && message.getStatus() != Message.STATUS_FAILED)
                    return;

                // TODO 如果是文件类型的，需要先上传，再发送

                // 我们在发送的时候需要通知界面更新状态，card;
                final MessageCard card = model.buildCard();
                Factory.getMessageCenter().dispatch(card);

                // 直接发送,进行网络调度
                RemoteService service = NetWork.remote();
                service.msgPush(model).enqueue(new Callback<RspModel<MessageCard>>() {
                    @Override
                    public void onResponse(Call<RspModel<MessageCard>> call, Response<RspModel<MessageCard>> response) {
                        RspModel<MessageCard> rspModel = response.body();
                        if (rspModel != null && rspModel.success()) {
                            MessageCard rspCard = rspModel.getResult();
                            if (rspCard != null) {
                                // 成功的调度
                                Factory.getMessageCenter().dispatch(rspCard);
                            }
                        } else {
                            // 检查是否是账户异常
                            Factory.decodeRspCode(rspModel, null);
                            // 走失败流程
                            onFailure(call, null);
                        }
                    }

                    @Override
                    public void onFailure(Call<RspModel<MessageCard>> call, Throwable t) {
                        // 发送失败
                        card.setStatus(Message.STATUS_FAILED);
                        Factory.getMessageCenter().dispatch(card);
                    }
                });
            }
        });
    }

    /**
     * 查询一个消息，这个消息是群的最后一个消息
     *
     * @param groupId 群id
     * @return 群聊的最后一条消息
     */
    public static Message findLastWithGroup(String groupId) {
        return SQLite.select()
                .from(Message.class)
                .where(Message_Table.group_id.eq(groupId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }

    /**
     * 查询一个消息，这个消息是某个用户的最后一个消息
     *
     * @param userId 用户id
     * @return 用户聊天的最后一条消息
     */
    public static Message findLastWithUser(String userId) {
        return SQLite.select()
                .from(Message.class)
                .where(OperatorGroup.clause()
                        .and(Message_Table.sender_id.eq(userId))
                        .and(Message_Table.group_id.isNull()))
                .or(Message_Table.receiver_id.eq(userId))
                .orderBy(Message_Table.createAt, false)
                .querySingle();
    }
}
