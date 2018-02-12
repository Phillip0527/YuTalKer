package com.im.yutalker.factory.model.api.message;

import com.im.yutalker.factory.model.card.MessageCard;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.persistence.Account;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Phillip on 2018/2/11.
 */

public class MsgCreateModel {

    // 从客户端生成，UUID
    private String id;

    // 内容
    private String content;

    // 附件
    private String attach;

    // 消息类型
    private int type = Message.TYPE_STR;

    // 接收者 可为空
    private String receiverId;

    // 接收者的类型，群，人
    private int receiverType = Message.RECEIVER_TYPE_NONE;

    private MsgCreateModel() {
        // 随机生成UUID
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }


    public String getContent() {
        return content;
    }


    public String getAttach() {
        return attach;
    }


    public int getType() {
        return type;
    }


    public String getReceiverId() {
        return receiverId;
    }


    public int getReceiverType() {
        return receiverType;
    }


    // TODO 当我们需要发送一个文件的时候，上传，content刷新的问题

    private MessageCard card;

    // 返回一个card
    public MessageCard buildCard() {
        if (card == null) {
            card = new MessageCard();
            card.setId(id);
            card.setContent(content);
            card.setAttach(attach);
            card.setType(type);
            card.setSenderId(Account.getUserId());
            // 如果是群
            if (receiverType == Message.RECEIVER_TYPE_GROUP) {
                card.setGroupId(receiverId);
            } else {
                card.setReceiverId(receiverId);
            }
            // 通过model建立的card就是一个初步状态
            card.setStatus(Message.STATUS_CREATED);
            card.setCreateAt(new Date());
        }
        return this.card;
    }

    /**
     * 建造者模式,快速建立一个发送的Model
     */
    public static class Builder {
        private MsgCreateModel model;

        public Builder() {
            this.model = new MsgCreateModel();
        }

        // 设置接收者
        public Builder receiver(String receiverId, int receiverType) {
            this.model.receiverId = receiverId;
            this.model.receiverType = receiverType;
            return this;
        }

        // 设置内容
        public Builder content(String content, int type) {
            this.model.content = content;
            this.model.type = type;
            return this;
        }

        // 设置附件
        public Builder attach(String attach) {
            this.model.attach = attach;
            return this;
        }

        public MsgCreateModel build() {
            return this.model;
        }
    }

    /**
     * 把一个Message的消息转换成一个为创建状态的CreateModel
     * @param message Message
     * @return MsgCreateModel
     */
    public static MsgCreateModel buildWithMessage(Message message) {
        MsgCreateModel model = new MsgCreateModel();
        model.id = message.getId();
        model.content = message.getContent();
        model.type = message.getType();
        model.attach = message.getAttach();

        // 如果接收者不为null，则是给人发送消息
        if (message.getReceiver() != null) {
            model.receiverId = message.getReceiver().getId();
            model.receiverType = Message.RECEIVER_TYPE_NONE;
        } else {
            model.receiverId = message.getGroup().getId();
            model.receiverType = Message.RECEIVER_TYPE_GROUP;
        }

        return model;
    }

}
