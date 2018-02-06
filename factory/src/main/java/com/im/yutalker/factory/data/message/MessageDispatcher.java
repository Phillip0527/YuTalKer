package com.im.yutalker.factory.data.message;

import android.text.TextUtils;

import com.im.yutalker.factory.data.helper.DbHelper;
import com.im.yutalker.factory.data.helper.GroupHelper;
import com.im.yutalker.factory.data.helper.MessageHelper;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.model.card.MessageCard;
import com.im.yutalker.factory.model.dp.Group;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 消息中心的实现类
 * Created by Phillip on 2018/2/1.
 */

public class MessageDispatcher implements MessageCenter {

    private static MessageCenter instance;

    // 单线程池,处理卡片，一个个的消息进行处理
    private final Executor executor = Executors.newSingleThreadExecutor();

    public static MessageCenter getInstance() {
        if (instance == null) {
            synchronized (MessageDispatcher.class) {
                if (instance == null)
                    instance = new MessageDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(MessageCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        // 丢到线程池中
        executor.execute(new MessageCardHandler(cards));
    }

    /**
     * 线程调度时触发run()方法
     */
    private class MessageCardHandler implements Runnable {
        private final MessageCard[] cards;

        MessageCardHandler(MessageCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            List<Message> messages = new ArrayList<>();
            for (MessageCard card : cards) {
                // 卡片基础信息过滤
                if (card == null || TextUtils.isEmpty(card.getSenderId())
                        || TextUtils.isEmpty(card.getId())
                        || (TextUtils.isEmpty(card.getReceiverId()) && TextUtils.isEmpty(card.getGroupId())))
                    continue;
                // 消息卡片有可能推送过来的，也有可能自己造的
                // 推送过来的：服务器一定有，我们可以查询的到（本地有可能有，有可能没有）
                // 直接造的：先存储本地，后发送网络
                // 发送消息流程：写消息 → 存储本地 → 发送网络 → 网络返回 → 刷新本地状态
                Message message = MessageHelper.findFromLocal(card.getId());
                if (message != null) {
                    // 如果本地有这个消息，同时本地消息状态为完成
                    if (message.getStatus() == Message.STATUS_DONE)
                        continue;

                    // 新状态为完成才更新服务器时间，不然不更新
                    if (card.getStatus() == Message.STATUS_DONE) {
                        // 代表网络发送成功，此时需要修改时间为服务器的时间
                        message.setCreateAt(card.getCreateAt());
                        // 如果没有进入判断代表发送失败了，不需要改时间
                    }
                    // 更新一些会变化的内容
                    message.setContent(card.getContent());
                    message.setAttach(card.getAttach());
                    // 更新状态
                    message.setStatus(card.getStatus());
                } else {
                    // 没找到消息，初次在数据库存储
                    User sender = UserHelper.search(card.getSenderId());
                    User receiver = null;
                    Group group = null;
                    if (!TextUtils.isEmpty(card.getReceiverId())) {
                        receiver = UserHelper.search(card.getReceiverId());
                    } else if (!TextUtils.isEmpty(card.getGroupId())) {
                        group = GroupHelper.findFromLocal(card.getGroupId());
                    }
                    // 接收者总有一个
                    if (receiver == null && group == null && sender != null)
                        continue;
                    message = card.build(sender, receiver, group);
                }
                messages.add(message);
            }
            if (messages.size() > 0)
                DbHelper.save(Message.class, messages.toArray(new Message[0]));
        }
    }
}
