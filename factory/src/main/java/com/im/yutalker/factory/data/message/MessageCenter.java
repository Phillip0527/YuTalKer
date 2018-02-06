package com.im.yutalker.factory.data.message;

import com.im.yutalker.factory.model.card.MessageCard;

/**
 * 消息中心，进行消息卡片的消费
 * Created by Phillip on 2018/2/1.
 */

public interface MessageCenter {
    // 分发处理一堆消息卡片的信息，并更新到数据库
    void dispatch(MessageCard... cards);
}
